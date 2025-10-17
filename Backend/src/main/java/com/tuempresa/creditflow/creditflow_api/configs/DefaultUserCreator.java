package com.tuempresa.creditflow.creditflow_api.configs;

import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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
        if (!userRepository.existsByEmail("admin@creditflow.com")) {
            String defaultPassword = "12345678Pro+";
            String encodedPassword = passwordEncoder.encode(defaultPassword);

            User florencia = User.builder()
                    .username("Florencia Rodríguez")
                    .firstName("Florencia")
                    .lastName("Rodríguez")
                    .password(encodedPassword)
                    .email("admin@creditflow.com")
                    .contact("+54 351-5654563")
                    .dni("94807935")
                    .birthDate(LocalDate.of(1990, 5, 15))
                    .country("Argentina")
                    .isActive(true)
                    .role(User.Role.OPERADOR)
                    .build();

            userRepository.save(florencia);
        }
    }
}