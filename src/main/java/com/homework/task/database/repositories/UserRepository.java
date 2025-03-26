package com.homework.task.database.repositories;

import com.homework.task.database.services.mappers.UserRowMapper;
import com.homework.task.database.templates.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * Saves a new user to the database.
     * This method inserts a new user into the 'users' table with the provided username, password, salt, and role.
     * It checks that both the username and password are provided and non-empty before attempting to save the user.
     * If a user with the same username already exists, a DuplicateKeyException is caught, and the method returns false.
     * If the user is successfully saved, it returns true.
     *
     * @param user - The user object containing the user's details (username, password, salt, role).
     * @return boolean - The result of function (true if the user is saved, false if not due to invalid data or duplication).
     */

    public boolean saveUser(User user) {
        String sql = "INSERT INTO users (username, password, salt, role) VALUES (?, ?, ?, ?)";
        if (user.getUsername() != null && !user.getUsername().isEmpty() &&
                user.getPassword() != null && !user.getPassword().isEmpty()) {
            try {
                return 0 < jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getSalt(), user.getRole().name());
            } catch (DuplicateKeyException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Retrieves a user from the database by their username.
     * This method queries the 'users' table to find a user with the specified username. If a user is found,
     * it returns the user object. If no user matches the given username, it returns null.
     *
     * @param username - The username of the user to be retrieved.
     * @return user - The user object if found, or null if no user matches the given username.
     */

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), username);
        if (users.isEmpty()) {
            return null;
        }
        return users.getFirst();
    }
}
