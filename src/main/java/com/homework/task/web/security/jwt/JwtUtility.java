package com.homework.task.web.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtility {

    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * Generates a JWT token for the given username.
     * This method creates a JWT token that contains the username as the subject. It sets the issued timestamp
     * and the expiration time to 10 hours from the current time. The token is signed using the HS256 algorithm
     * with a secret key.
     *
     * @param username - The username for which the JWT token is generated.
     * @return String - A string representing the generated JWT token.
     */

    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Extracts the username from the given JWT token.
     * This method extracts the username (subject) from the JWT token by calling the `extractAllClaims` method
     * and retrieving the subject claim.
     *
     * @param token - The JWT token from which the username is extracted.
     * @return String - The username extracted from the JWT token.
     */

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Validates the given JWT token for the specified user details.
     * This method checks if the username in the token matches the username of the user details and whether
     * the token has expired.
     *
     * @param token - The JWT token to be validated.
     * @param userDetails - The user details used to verify the token's validity.
     * @return boolean - True if the token is valid (username matches and token is not expired), false otherwise.
     */

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Extracts all claims from the given JWT token.
     * This method parses the JWT token and retrieves all claims, such as the subject, issued date, expiration, etc.
     * It uses the secret key for verifying the signature of the token.
     *
     * @param token - The JWT token from which claims are extracted.
     * @throws SignatureException - If the token's signature is invalid.
     * @return Claims - A `Claims` object containing all claims from the JWT token.
     */

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            throw new SignatureException("Invalid JWT signature", e);
        }

    }

    /**
     * Checks if the given JWT token has expired.
     * This method checks the expiration date of the token and compares it with the current date.
     * It returns `true` if the token has expired and `false` if it is still valid.
     *
     * @param token - The JWT token to check for expiration.
     * @return boolean - True if the token is expired, false otherwise.
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

}
