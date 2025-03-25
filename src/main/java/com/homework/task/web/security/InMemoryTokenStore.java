package com.homework.task.web.security;

import com.homework.task.web.security.interfaces.TokenStore;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryTokenStore implements TokenStore {
    private final ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();

    @Override
    public void saveToken(String username, String token, long expiryTime) {
        // Store the token with an expiry mechanism (optional)
        tokens.put(username, token);
    }

    @Override
    public Optional<String> getToken(String username) {
        return Optional.ofNullable(tokens.get(username));
    }
}
