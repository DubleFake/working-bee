package com.homework.task.web.security.jwt;

import com.homework.task.database.services.TokenBlacklistService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtility jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * Constructs a JwtRequestFilter with the specified JWT utility and user details service.
     * This constructor initializes the filter with the necessary dependencies:
     * - `JwtUtility` for handling JWT token operations.
     * - `UserDetailsService` for loading user details based on the username.
     *
     * @param jwtUtil - The utility class for managing JWT token operations (e.g., extracting username, validating token).
     * @param userDetailsService - The service used to load user details by username.
     */

    public JwtRequestFilter(JwtUtility jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filters HTTP requests to check the presence and validity of a JWT token in the "Authorization" header.
     * This method attempts to extract a JWT token from the request header and validate it. If the token is valid and not blacklisted,
     * it sets the user authentication in the SecurityContext. If the token is invalid or blacklisted, it returns an HTTP 401 (Unauthorized) error.
     *
     * @param request - The HTTP request to be filtered.
     * @param response - The HTTP response that can be modified if the token is invalid.
     * @param chain - The filter chain that continues the request processing.
     * @throws ServletException - If an error occurs during the filtering process.
     * @throws IOException - If an error occurs while handling the HTTP request or response.
     * @throws SignatureException - If the JWT signature is invalid.
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException, SignatureException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && (authorizationHeader.startsWith("Bearer ") || authorizationHeader.startsWith("bearer "))) {
            jwt = authorizationHeader.substring(7);
            if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
            try {
                // Attempt to extract the username, which will throw SignatureException if invalid
                username = jwtUtil.extractUsername(jwt);
            } catch (SignatureException e) {
                // Rethrow SignatureException to be handled by ControllerAdvice
                throw new SignatureException("Invalid JWT signature", e);
            } catch (JwtException e) {
                // Handle other JWT exceptions if needed
                throw new JwtException("JWT processing failed", e);
            }
        }

        // Continue with setting up user authentication if username extraction was successful
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        chain.doFilter(request, response);
    }
}
