package com.homework.task.database.services;

import com.homework.task.database.repositories.UserRepository;
import com.homework.task.database.templates.User;
import com.homework.task.database.templates.UserRequest;
import com.homework.task.web.security.PasswordManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

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

    public boolean login(UserRequest userRequest) {
        User user = userRepository.findByUsername(userRequest.getUsername());
        try {
            if (user != null && PasswordManager.verifyPassword(userRequest.getPassword(), user.getSalt() + ":" + user.getPassword())) {
                return true;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int logout() {
        return 1;
    }

}
