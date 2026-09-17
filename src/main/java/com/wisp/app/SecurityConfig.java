package com.wisp.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

                http
                                .authorizeHttpRequests(auth -> auth

                                                // Recursos públicos
                                                .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**", "/error").permitAll()

                                                // Login y página de error
                                                .requestMatchers("/login", "/acceso-denegado").permitAll()

                                                // Dashboard solo ADMIN
                                                .requestMatchers("/dashboard").hasRole("ADMIN")

                                                // Solo visualizar instalaciones
                                                .requestMatchers("/instalaciones", "/instalaciones/ver/**", "/averias",
                                                                "/averias/ver/**")
                                                .hasAnyRole("ADMIN", "Tecnico")

                                                // Operaciones de mantenimiento de instalaciones
                                                .requestMatchers(
                                                                "/instalaciones/nuevo",
                                                                "/instalaciones/guardar",
                                                                "/instalaciones/editar/**",
                                                                "/instalaciones/eliminar/**",
                                                                "/averias/nuevo",
                                                                "/averias/guardar",
                                                                "/averias/editar/**",
                                                                "/averias/eliminar/**",
                                                                "/pagos/**")
                                                .hasRole("ADMIN")

                                                // Módulos exclusivos ADMIN
                                                .requestMatchers(
                                                                "/clientes/**",
                                                                "/averias/**",
                                                                "/stock/**",
                                                                "/usuarios/**")
                                                .hasRole("ADMIN")

                                                .anyRequest().authenticated())

                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/acceso-denegado"))

                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/instalaciones", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())

                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true")
                                                .permitAll())

                                .headers(headers -> headers
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives(
                                                                                "default-src 'self'; " +
                                                                                "script-src 'self' 'unsafe-inline'; " +
                                                                                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdn.jsdelivr.net data:; " +
                                                                                "img-src 'self' data:; " +
                                                                                "font-src 'self' https://fonts.gstatic.com https://cdn.jsdelivr.net; " +
                                                                                "connect-src 'self'; " +
                                                                                "frame-ancestors 'none'")));

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
