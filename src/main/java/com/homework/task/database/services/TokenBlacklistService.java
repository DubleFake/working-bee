package com.homework.task.database.services;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {

    /**
     * A set to store blacklisted tokens.
     * This set is used to track tokens that are considered blacklisted, meaning they are no longer valid for use.
     * Tokens can be added to this set via the `blacklistToken` method and checked for validity using the `isTokenBlacklisted` method.
     */
    private final Set<String> blacklistedTokens = new HashSet<>();

    /**
     * Adds a token to the blacklist.
     * This method adds the provided token to the set of blacklisted tokens, marking it as invalid and preventing it
     * from being used for authentication or authorization purposes.
     *
     * @param token - The token to be added to the blacklist.
     */

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    /**
     * Checks if a token is blacklisted.
     * This method checks if the provided token is in the set of blacklisted tokens. If the token is found in the set,
     * it is considered blacklisted and the method returns true; otherwise, it returns false.
     *
     * @param token - The token to be checked.
     * @return boolean - True if the token is blacklisted, false otherwise.
     */

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

}
