package com.tuempresa.creditflow.creditflow_api.service.impl;

import com.tuempresa.creditflow.creditflow_api.dtos.BaseResponse;
import com.tuempresa.creditflow.creditflow_api.dtos.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dtos.user.*;
import com.tuempresa.creditflow.creditflow_api.exception.userExc.EmailNotFoundException;
import com.tuempresa.creditflow.creditflow_api.exception.userExc.InvalidCredentialsException;
import com.tuempresa.creditflow.creditflow_api.exception.userExc.UserDisabledException;
import com.tuempresa.creditflow.creditflow_api.jwt.JwtService;
import com.tuempresa.creditflow.creditflow_api.mapper.UserMapper;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.repository.UserRepository;
import com.tuempresa.creditflow.creditflow_api.service.AuthService;
import com.tuempresa.creditflow.creditflow_api.service.api.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserMapper userMapper;

    // Método login
    @Override
    @Transactional(readOnly = true)
    public ExtendedBaseResponse<AuthResponseDto> login(LoginRequestDto request) {
        String email = request.email();
        String password = request.password();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Usuario no encontrado"));

        if (!user.getIsActive()) {
            throw new UserDisabledException("Usuario suspendido temporalmente");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Contraseña inválida");
        }

        String token = jwtService.getToken(user);
        var response = userMapper.toAuthResponse(user);

        return ExtendedBaseResponse.of(
                BaseResponse.ok("Login exitoso."),
                new AuthResponseDto(response.id(),response.firstName(),response.lastName(), response.username(), token, response.role())
        );
    }
    @Override
    @Transactional
    public ExtendedBaseResponse<AuthResponseDto> register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email ya registrado");
        }

        // 1. Generar contraseña aleatoria
        String generatedPassword = UUID.randomUUID().toString().substring(0, 8);

        // 2. Crear el usuario
        String username = request.firstName() + " " + request.lastName();
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(username)
                .password(passwordEncoder.encode(generatedPassword))
                .email(request.email())
                .contact(request.contact())
                .isActive(Boolean.TRUE)
                .role(User.Role.PYME)
                .wantsEmailNotifications(Boolean.TRUE)
                .build();

        userRepository.save(user);

        // 3. Intentar enviar credenciales por email (sin romper si falla)
        String subject = "🎉 Bienvenido a la plataforma Credit - Flow";
        String body = String.format("""
            ¡Hola %s! 👋

            Se ha creado una cuenta para vos en nuestra plataforma.

            Aquí están tus credenciales de acceso:

            📧 Email: %s
            🔑 Contraseña: %s

            Te recomendamos cambiar la contraseña una vez hayas iniciado sesión.

            ¡Gracias por unirte! 🚀
            """, username, request.email(), generatedPassword);

        try {
            emailService.sendEmail(user.getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("⚠️ Error enviando correo de bienvenida: " + e.getMessage());
            // No hacemos throw, así el flujo de registro sigue normalmente
        }

        // 4. Retornar la respuesta sin token (solo confirmación del alta)
        var response = userMapper.toAuthResponse(user);

        return ExtendedBaseResponse.of(
                BaseResponse.created("Usuario creado correctamente. (El envío de correo puede haber fallado en el servidor)."),
                new AuthResponseDto(response.id(), response.username(), response.firstName(), response.lastName(), null, response.role())
        );
    }


    // Método resetPassword
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {
        User user = userRepository.findByResetToken(request.token())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setResetToken(null);
        userRepository.save(user);
    }

    // Método generatePasswordResetToken
    @Override
    @Transactional
    public ExtendedBaseResponse<String> generatePasswordResetToken(EmailDto email) {
        User user = userRepository.findByEmail(email.email())
                .orElseThrow(() -> new EmailNotFoundException("🚫 Usuario no encontrado"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        emailService.sendEmail(
                user.getEmail(),
                "🔒 Restablecer contraseña",
                "📩 Tu código de verificación: " + token
        );

        return ExtendedBaseResponse.of(BaseResponse.ok("✅ Token generado con éxito"), token);
    }
}
