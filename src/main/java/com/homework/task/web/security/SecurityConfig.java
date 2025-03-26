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

    /**
     * Constructs a SecurityConfig object with the provided JwtRequestFilter.
     * This constructor initializes the security configuration by injecting the `JwtRequestFilter`,
     * which is responsible for handling JWT authentication. This filter will be applied before
     * `UsernamePasswordAuthenticationFilter` in the security filter chain.
     *
     * @param jwtRequestFilter The JWT request filter used to intercept and validate requests with JWT tokens.
     */

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Configures the security filter chain for the application.
     * This method sets up HTTP security by disabling CSRF protection, configuring stateless session management,
     * and defining authorization rules for specific endpoints. It also adds the JWT filter before the
     * `UsernamePasswordAuthenticationFilter` and disables basic authentication, form login, and Spring's logout handling.
     *
     * @param http - The HttpSecurity object used to configure security settings.
     * @throws Exception - If an error occurs during the configuration process.
     * @return SecurityFilterChain - A configured SecurityFilterChain object that is used by Spring Security to process HTTP requests.
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
