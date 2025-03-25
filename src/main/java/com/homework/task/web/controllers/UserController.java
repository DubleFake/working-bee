package com.homework.task.web.controllers;

import com.homework.task.database.services.UserService;
import com.homework.task.database.templates.Task;
import com.homework.task.database.templates.UserRequest;
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

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRequest userRequest) {
        if (userService.createUser(userRequest) == 1) {
            return new ResponseEntity<>("Created.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Bad request.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequest userRequest) throws NoSuchAlgorithmException {
        String token = userService.login(userRequest);
        if (token.isEmpty()) {
            return new ResponseEntity<>("Forbidden.", HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>("bearer:" + token, HttpStatus.OK);
        }
    }

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
            return new ResponseEntity<>("Forbidden.", HttpStatus.FORBIDDEN);
        }
    }
}
