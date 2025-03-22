package com.homework.task.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int saveTask(Task task) {
        String sql = "INSERT INTO tasks (name, description) VALUES (?, ?)";
        return jdbcTemplate.update(sql, task.getName(), task.getDescription());
    }

}
