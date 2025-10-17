package com.tuempresa.creditflow.creditflow_api.security;

import com.tuempresa.creditflow.creditflow_api.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC ENDPOINTS --------------------------------------------------
                        .requestMatchers(publicEndpoints()).permitAll()
                        // AUTH ENDPOINTS ---------------------------------------------------
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/generate-reset-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/oauth-success").permitAll()
                        // USER ENDPOINTS ---------------------------------------------------
                        .requestMatchers(HttpMethod.GET, "/user/{id}").hasAnyAuthority("OPERADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/user/update").hasAnyAuthority("ADMIN", "OPERADOR", "PYME")
                        .requestMatchers(HttpMethod.GET, "/user/list").hasAnyAuthority("OPERADOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/user/list-active").hasAnyAuthority("OPERADOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/user/change-status").hasAnyAuthority("OPERADOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/user/{id}").hasAuthority("SUPER_ADMIN")

                        // KYC VERIFICATIONS -----------------------------------------------------
                        .requestMatchers(HttpMethod.POST, "/api/kyc/start").hasAnyAuthority("PYME")

                        // COMPANY  -----------------------------------------------------
                        .requestMatchers(HttpMethod.POST, "/api/companies").hasAnyAuthority("PYME")
                        .requestMatchers(HttpMethod.GET, "/api/companies").hasAnyAuthority("PYME")

                        // CREDIT  -----------------------------------------------------
                        .requestMatchers(HttpMethod.POST, "/api/credit-applications").hasAnyAuthority("PYME")
                        .requestMatchers(HttpMethod.GET, "/api/credit-applications").hasAnyAuthority("PYME")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/auth/login") // Ruta de login si querés una custom
                        .defaultSuccessUrl("/auth/oauth-success", true)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            String jsonResponse = String.format(
                                    "{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"%s\"}",
                                    "No autorizado: Token JWT requerido"
                            );
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(jsonResponse);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            String jsonResponse = String.format(
                                    "{\"status\": 403, \"error\": \"Forbidden\", \"message\": \"%s\"}",
                                    "Acceso denegado: No tienes permisos suficientes"
                            );
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(jsonResponse);
                        })
                )
                .build();
    }

    private static String[] publicEndpoints() {
        return new String[]{
                "/api-docs/**",
                "/swagger-ui/**",
                "/api-docs.yaml",
                "/webjars/**",
                "/swagger-ui-custom.html",
                "/public/lead/create",
                "/auth/oauth-success",
                "/ping/pong",
                "/public/**",
                "/auth/register"
        };
    }
}
