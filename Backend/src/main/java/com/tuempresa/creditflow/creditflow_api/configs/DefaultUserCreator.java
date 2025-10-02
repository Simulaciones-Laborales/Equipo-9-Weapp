package com.tuempresa.creditflow.creditflow_api.configs;

import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserCreator implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserCreator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("Florencia Galeassi")) {
            String defaultPassword = "12345678Pro+";
            String encodedPassword = passwordEncoder.encode(defaultPassword);

            User florencia = User.builder()
                    .firstName("Florencia")
                    .lastName("Rodríguez")
                    .password(encodedPassword)
                    .email("Florencia_Galeassi@example.com")
                    .contact("+54 351-5654563")
                    .isActive(true)
                    .role(User.Role.SUPER_ADMIN)
                    .wantsEmailNotifications(false)
                    .build();

            userRepository.save(florencia);
        }
    }
}