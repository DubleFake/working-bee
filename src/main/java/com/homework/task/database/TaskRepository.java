package com.homework.task.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int saveTask(Task task) {
        String sql = "INSERT INTO tasks (name, description, status) VALUES (?, ?, ?)";
        if (task.getName() != null && !task.getName().isEmpty() && task.getStatus() != null) {
            return jdbcTemplate.update(sql, task.getName(), task.getDescription(), task.getStatus().name());
        } else {
            return 0;
        }

    }

    public Task findById(long id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        jdbcTemplate.query
        return jdbcTemplate.query(sql, new TaskRowMapper(), id);
    }

    public List<Task> getTasksFilteredByStatus(Task.Status status) {
        String sql = "SELECT * FROM tasks WHERE status = ?";
        return jdbcTemplate.query(sql, new TaskRowMapper(), status.name());
    }

}
