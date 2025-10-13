package com.tuempresa.creditflow.creditflow_api.repository;

import com.tuempresa.creditflow.creditflow_api.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    List<User> findAllByRole(User.Role role);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    List<User> findByIsActiveTrueOrderByUsernameAsc();

    boolean existsByContact(@Pattern(
                regexp = "^\\+?\\d{1,4}[\\s-]?\\d{1,4}[\\s-]?\\d{4,10}$",
                message = "El contacto debe estar en formato válido, por ejemplo: +54 015-68062288"
        ) @NotBlank(message = "El contacto no puede estar en blanco") @Size(max = 20, message = "El contacto no puede exceder los 20 caracteres") String contact);

    boolean existsByDni(@NotBlank(message = "El DNI no puede estar en blanco") @Size(max = 20, message = "El DNI no puede exceder los 20 caracteres") String dni);
}
