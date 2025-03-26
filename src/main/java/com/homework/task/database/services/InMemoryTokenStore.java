package com.homework.task.database.services;

import com.homework.task.web.security.interfaces.TokenStore;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryTokenStore implements TokenStore {
    private final ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();

    /**
     * Adds a token to the token list.
     * This method adds the provided token to the set created tokens for later retrieval and use.
     *
     * @param token - The token to be added to the token list.
     */

    @Override
    public void saveToken(String username, String token, long expiryTime) {
        tokens.put(username, token);
    }

    /**
     * Retrieves a token to the token list.
     * This method retrieves token from the list that is associated with provided username.
     *
     * @param username - Username with which the token is associated.
     * @return Optional<String> - Returns an 'Optional' of type string with the token in it or
     *                            an empty optional if token was not found.
     */

    @Override
    public Optional<String> getToken(String username) {
        return Optional.ofNullable(tokens.get(username));
    }
}
