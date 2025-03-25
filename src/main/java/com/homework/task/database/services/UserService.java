package com.homework.task.database.services;

import com.homework.task.database.repositories.UserRepository;
import com.homework.task.database.templates.User;
import com.homework.task.database.templates.UserRequest;
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

    @Autowired
    private final TokenStore tokenStore;

    public UserService(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    public int createUser(UserRequest userRequest) {
        User user = new User();
        try {
            String[] saltPasswordCombo = PasswordManager.hashPassword(userRequest.getPassword()).split(":");
            user.setUsername(userRequest.getUsername());
            user.setSalt(saltPasswordCombo[0]);
            user.setPassword(saltPasswordCombo[1]);
            user.setRole(User.Role.USER);
            return userRepository.saveUser(user);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String login(UserRequest userRequest) throws NoSuchAlgorithmException {
        User user = userRepository.findByUsername(userRequest.getUsername());
            if (user != null && PasswordManager.verifyPassword(userRequest.getPassword(), user.getSalt() + ":" + user.getPassword())) {
                Optional<String> existingToken = tokenStore.getToken(userRequest.getUsername());
                if (existingToken.isPresent() && !jwtUtil.isTokenExpired(existingToken.get()) && !tokenBlacklistService.isTokenBlacklisted(existingToken.get())) {
                    // If a valid token is found, blacklist it
                    tokenBlacklistService.blacklistToken(existingToken.get());
                }

                String token = jwtUtil.generateToken(userRequest.getUsername());
                tokenStore.saveToken(userRequest.getUsername(), token, 3600); // 1 hour expiry
                // If password is valid, generate JWT token
                return jwtUtil.generateToken(userRequest.getUsername());
            }
        return "";
    }

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
