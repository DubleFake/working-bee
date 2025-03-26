package com.homework.task.web.controllers;

import com.homework.task.database.services.UserService;
import com.homework.task.database.templates.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    /**
     * Registers a new user by creating their account in the system.
     * This method receives a `User` object with the user's details, then delegates the user creation process to the
     * `userService.createUser` method. If the user is successfully created, it returns HTTP 201 (Created). If there is
     * an issue with the request (e.g., invalid user details), it returns HTTP 400 (Bad Request).
     *
     * @param user - The user object containing the user's details (username, password, etc.).
     * @return ResponseEntity - A response entity indicating the success or failure of the user registration process.
     */

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userService.createUser(user)) {
            return new ResponseEntity<>("Created.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Bad request.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Authenticates a user and returns a JWT token.
     * This method receives a `User` object with the user's login credentials, then delegates the login process to the
     * `userService.login` method. If the login is successful, it returns a JWT token with HTTP 200 (OK). If the credentials
     * are invalid or there is an error, it returns HTTP 400 (Bad Request).
     *
     * @param user - The user object containing the user's login credentials (username and password).
     * @throws NoSuchAlgorithmException - If there is an error verifying the password.
     * @return ResponseEntity - A response entity containing the JWT token with a "bearer:" prefix if login is successful
     *         or an error message if login fails.
     */

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) throws NoSuchAlgorithmException {
        String token = userService.login(user);
        if (token.isEmpty()) {
            return new ResponseEntity<>("Bad request.", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>("bearer:" + token, HttpStatus.OK);
        }
    }

    /**
     * Logs out the user by invalidating their JWT token.
     * This method retrieves the currently authenticated user's username from the SecurityContext and calls the
     * `userService.logout` method to invalidate the user's token. If the logout is successful, it returns HTTP 200 (OK).
     * If the user is not authorized or there is an issue with the token, it returns HTTP 401 (Unauthorized).
     *
     * @return ResponseEntity - A response entity indicating the success or failure of the logout process.
     */

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = authentication.getPrincipal().toString();
        }

        if (userService.logout(username)) {
            return new ResponseEntity<>("OK.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unauthorized.", HttpStatus.UNAUTHORIZED);
        }
    }
}
