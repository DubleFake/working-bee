package com.homework.task.database.services.mappers;

import com.homework.task.database.templates.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {

    /**
     * Maps a row from the result set to a User object.
     * This method is used to convert a row of data from a SQL result set into a User object. It retrieves the values
     * from the columns (id, username, password, role, salt) in the result set and sets them in a new User object.
     * The role is mapped from a string value to the corresponding User.Role enum.
     *
     * @param rs - The result set containing the data from the database query.
     * @param rowNum - The row number in the ResultSet (used for pagination or processing multiple rows).
     * @throws SQLException If there is an error accessing the result set.
     * @return user - A User object populated with the data from the current row of the result set.
     */

    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setSalt(rs.getString("salt"));
        return user;
    }
}
