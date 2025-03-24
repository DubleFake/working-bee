package com.homework.task.web.controllers;

import com.homework.task.database.services.UserService;
import com.homework.task.database.templates.Task;
import com.homework.task.database.templates.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRequest userRequest) {
            return new ResponseEntity<>("Created.",HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequest userRequest) {
        return new ResponseEntity<>("OK.",HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody UserRequest userRequest) {
        return new ResponseEntity<>("OK.",HttpStatus.OK);
    }
}
