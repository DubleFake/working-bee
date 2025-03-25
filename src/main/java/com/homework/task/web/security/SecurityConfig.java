package com.homework.task.web.security;

import com.homework.task.web.security.jwt.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

/**
 * Configures the security settings for HTTP requests in a Spring application using Spring Security's HttpSecurity.
 * This method disables CSRF protection for API-only setups and defines access rules for specific URLs.
 * Public endpoints are explicitly allowed, and all other requests are restricted to authenticated users.
 *
 * @param http - The HttpSecurity object that is used to customize web-based security for HTTP requests.
 * @throws Exception - The method can throw an exception during the configuration of security settings, particularly if any invalid or conflicting configurations are encountered.
 * @return SecurityFilterChain - Returns a configured SecurityFilterChain object that is used to manage security filters for HTTP requests.
 *
 */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for API-only setup
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll() // Public endpoints
                        .anyRequest().authenticated() // Secure all other endpoints
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class) // JWT filter
                .httpBasic(AbstractHttpConfigurer::disable) // Disable basic auth
                .formLogin(AbstractHttpConfigurer::disable) // Disable form login
                .logout(AbstractHttpConfigurer::disable);   // Disable Spring handled logout

        return http.build();
    }

}
