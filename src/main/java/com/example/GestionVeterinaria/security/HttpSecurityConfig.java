package com.example.GestionVeterinaria.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class HttpSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Tras iniciar sesión cada rol va a su propia pantalla:
     * ADMIN -> panel de administración, VETERINARIO -> su agenda,
     * RECEPCION -> dashboard general.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String destino = "/panel";

            boolean esAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            boolean esVeterinario = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_VETERINARIO".equals(a.getAuthority()));

            if (esAdmin) {
                destino = "/panel";
            } else if (esVeterinario) {
                destino = "/veterinarios/agenda";
            }

            response.sendRedirect(request.getContextPath() + destino);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationSuccessHandler authenticationSuccessHandler) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {

                    // Rutas públicas de la landing: la home y una por sección.
                    String[] rutasLanding = {
                            "/", "/inicio", "/servicios", "/pacientes", "/contacto", "/ubicacion" };

                    // Recursos públicos (la de login necesita el logo sin sesión)
                    auth.requestMatchers(rutasLanding)
                            .permitAll();
                    auth.requestMatchers("/login", "/error",
                                    "/css/**", "/js/**", "/images/**", "/img/**")
                            .permitAll();

                    // ADMIN
                    auth.requestMatchers("/panel/**")
                            .hasRole("ADMIN");

                    // VETERINARIO
                    auth.requestMatchers("/veterinarios/**", "/historial/**")
                            .hasRole("VETERINARIO");

                    // RECEPCION puede gestionar citas
                    auth.requestMatchers("/citas/**")
                            .hasAnyRole("ADMIN", "RECEPCION");

                    // Todo lo demás requiere login
                    auth.anyRequest().authenticated();
                })
                // Página de login propia (templates/login.html)
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(authenticationSuccessHandler)
                        .failureUrl("/login?error"))
                .logout(LogoutConfigurer::permitAll)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

}
