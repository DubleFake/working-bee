package com.homework.task.web.security.interfaces;

import java.util.Optional;

public interface TokenStore {
    void saveToken(String username, String token, long expiryTime);
    Optional<String> getToken(String username);
    boolean isTokenValid(String token);
}
