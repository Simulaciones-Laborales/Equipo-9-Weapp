package com.tuempresa.creditflow.creditflow_api.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {
    /**
     * Devuelve el principal del usuario logueado (email, username o token).
     * Retorna null si no hay usuario autenticado.
     */
    public String getLoggedInPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        // Siempre será el email porque es el username
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername(); // Esto es el email
        }
        if (principal instanceof String) {
            return (String) principal; // Esto también debería ser el email
        }

        return null;
    }
}
