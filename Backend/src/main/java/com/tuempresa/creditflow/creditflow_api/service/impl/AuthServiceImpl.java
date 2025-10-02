package com.tuempresa.creditflow.creditflow_api.service.impl;

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
                new AuthResponseDto(response.id(), response.username(), token, response.role(), response.userImage())
        );
    }

    // Método register
    @Override
    @Transactional
    public ExtendedBaseResponse<AuthResponseDto> register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email ya registrado");
        }

        // 1. Generar contraseña aleatoria
        String generatedPassword = UUID.randomUUID().toString().substring(0, 8); // Ej: 8 caracteres

        // 2. Crear el usuario con esa contraseña
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(generatedPassword))
                .email(request.email())
                .contact(request.contact())
                .isActive(Boolean.TRUE)
                .role(User.Role.CLIENT)
                .type(request.type())
                .subscribedToNewsletter(Boolean.TRUE)
                .wantsEmailNotifications(Boolean.TRUE)
                .build();

        userRepository.save(user);

        // 3. Enviar credenciales por email
        String subject = "🎉 Bienvenido a la plataforma Killa Deco";
        String body = String.format("""
        ¡Hola %s! 👋

        Se ha creado una cuenta para vos en nuestra plataforma.

        Aquí están tus credenciales de acceso:

        📧 Email: %s
        🔑 Contraseña: %s

        Te recomendamos cambiar la contraseña una vez hayas iniciado sesión.

        ¡Gracias por unirte! 🚀
        """, request.username(), request.email(), generatedPassword);

        emailService.sendEmail(user.getEmail(), subject, body);

        // 4. Retornar la respuesta (sin token porque el usuario aún no inició sesión)
        var response = userMapper.toAuthResponse(user);

        return ExtendedBaseResponse.of(
                BaseResponse.created("Usuario creado correctamente. Credenciales enviadas por email."),
                new AuthResponseDto(response.id(), response.username(), null, response.role(), null)
        );
    }

    // Método resetPassword
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
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
