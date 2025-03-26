package com.homework.task.database.services;

import com.homework.task.database.repositories.UserRepository;
import com.homework.task.database.templates.User;
import com.homework.task.web.security.PasswordManager;
import com.homework.task.web.security.interfaces.TokenStore;
import com.homework.task.web.security.jwt.JwtUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JwtUtility jwtUtil;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    private final TokenStore tokenStore;

    /**
     * Constructs a UserService with the specified TokenStore.
     * The constructor injects a TokenStore to manage JWT token storage.
     *
     * @param tokenStore - The TokenStore used to store and retrieve tokens for users.
     */

    public UserService(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    /**
     * Creates a new user and saves them to the repository.
     * This method hashes the user's password, sets the user's role to 'USER', and saves the new user to the repository.
     * If password hashing fails, it returns false.
     *
     * @param user - The user object containing the new user's details.
     * @return boolean - True if the user is successfully created and saved, false if an error occurs during the process.
     */

    public boolean createUser(User user) {
        User newUser = new User();
        try {
            String[] saltPasswordCombo = PasswordManager.hashPassword(user.getPassword()).split(":");
            newUser.setUsername(user.getUsername());
            newUser.setSalt(saltPasswordCombo[0]);
            newUser.setPassword(saltPasswordCombo[1]);
            newUser.setRole(User.Role.USER);
            return userRepository.saveUser(newUser);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Logs in a user by verifying their credentials and generating a JWT token.
     * This method checks the user's username and password, and if they are correct, it generates and returns a JWT token.
     * If a valid token already exists for the user, it blacklists the old token before generating a new one.
     *
     * @param user - The user object containing the user's login credentials.
     * @throws NoSuchAlgorithmException - If password verification fails due to an algorithm issue.
     * @return String - A JWT token if the login is successful, or an empty string if the login fails.
     */

    public String login(User user) throws NoSuchAlgorithmException {
        User existingUser = userRepository.findByUsername(user.getUsername());
            if (existingUser != null && PasswordManager.verifyPassword(user.getPassword(), existingUser.getSalt() + ":" + existingUser.getPassword())) {
                Optional<String> existingToken = tokenStore.getToken(user.getUsername());
                if (existingToken.isPresent() && !jwtUtil.isTokenExpired(existingToken.get()) && !tokenBlacklistService.isTokenBlacklisted(existingToken.get())) {
                    // If a valid token is found, blacklist it
                    tokenBlacklistService.blacklistToken(existingToken.get());
                }

                String token = jwtUtil.generateToken(user.getUsername());
                tokenStore.saveToken(user.getUsername(), token, 3600); // 1 hour expiry
                // If password is valid, generate JWT token
                return jwtUtil.generateToken(user.getUsername());
            }
        return "";
    }

    /**
     * Logs out the user by blacklisting their existing JWT token.
     * This method checks if a valid token exists for the user, and if it does, it blacklists the token to prevent future use.
     *
     * @param username - The username of the user logging out.
     * @return boolean - True if the logout is successful and the token is blacklisted, false if no valid token is found.
     */

    public boolean logout(String username) {
        Optional<String> existingToken = tokenStore.getToken(username);
        if (existingToken.isPresent() && !tokenBlacklistService.isTokenBlacklisted(existingToken.get())) {
            // If a valid token is found, blacklist it
            tokenBlacklistService.blacklistToken(existingToken.get());
            return true;
        } else {
            return false;
        }
    }

}
