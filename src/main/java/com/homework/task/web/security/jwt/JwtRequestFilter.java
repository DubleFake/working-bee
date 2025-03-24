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

    public JwtRequestFilter(JwtUtility jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException, SignatureException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
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
