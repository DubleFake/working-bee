package com.homework.task.database;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskRowMapper implements RowMapper<Task> {

    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setName(rs.getString("name"));;
        task.setDescription(rs.getString("description"));
        task.setStatus(Task.Status.valueOf(rs.getString("status")));
        return task;
    }

}
