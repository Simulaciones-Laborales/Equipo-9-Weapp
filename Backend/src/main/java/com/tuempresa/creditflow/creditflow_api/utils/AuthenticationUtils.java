package com.tuempresa.creditflow.creditflow_api.utils;

import com.tuempresa.creditflow.creditflow_api.dto.BaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.ExtendedBaseResponse;
import com.tuempresa.creditflow.creditflow_api.dto.user.UserDto;
import com.tuempresa.creditflow.creditflow_api.model.User;
import com.tuempresa.creditflow.creditflow_api.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthenticationUtils {
    private final IUserService userService;

    public AuthenticationUtils(IUserService userService) {
        this.userService = userService;
    }

    public UUID getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // O podrías lanzar una excepción aquí
        }

        Object principal = authentication.getPrincipal();
        UUID loggedInUserId = null;

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            // Asume que tienes un método en tu UserService para obtener el ID por username
            loggedInUserId = userService.getUserIdByEmail(userDetails.getUsername());
        } else if (principal instanceof String) {
            loggedInUserId = userService.getUserIdByEmail((String) principal);
        }

        return loggedInUserId;
    }

    public ExtendedBaseResponse<UUID> getLoggedInUserIdResponse() {
        UUID userId = getLoggedInUserId();
        if (userId == null) {
            return ExtendedBaseResponse.of(BaseResponse.error(HttpStatus.UNAUTHORIZED, "Usuario no autenticado."), null);
        }
        return ExtendedBaseResponse.of(BaseResponse.ok("Usuario autenticado"), userId);
    }

    public ExtendedBaseResponse<UserDto> findUserById(UUID id){
        return this.userService.findUserById(id);
    }

    /**
     * Obtiene toda la data del usuario autenticado.
     *
     * @return devuelve al usuario autenticado como un {@link User}.
     * @author David Lugo - Refactorizado por Alben Bustamante.
     */
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findEntityByEmail(email);
    }
}
