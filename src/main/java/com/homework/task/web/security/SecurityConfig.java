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
