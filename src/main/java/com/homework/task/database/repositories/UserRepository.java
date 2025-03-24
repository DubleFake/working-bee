package com.homework.task.database.repositories;

import com.homework.task.database.services.mappers.UserRowMapper;
import com.homework.task.database.templates.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int saveUser(User user) {
        String sql = "INSERT INTO users (username, password, salt, role) VALUES (?, ?, ?, ?)";
        if (user.getUsername() != null && !user.getUsername().isEmpty() &&
                user.getPassword() != null && !user.getPassword().isEmpty()) {
            return jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getSalt(), user.getRole().name());
        } else {
            return 0;
        }
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), username);
        if (users.isEmpty()) {
            return null;
        }
        return users.getFirst();
    }
}
