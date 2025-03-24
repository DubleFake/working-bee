package com.homework.task.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/tasks", "/login", "/register","/tasks/*").permitAll() // Public endpoints
                        .anyRequest().authenticated() // Secure all other endpoints
                );

        return http.build();
    }

}
