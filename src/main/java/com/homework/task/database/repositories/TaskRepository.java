package com.homework.task.database.repositories;

import com.homework.task.database.templates.Task;
import com.homework.task.database.services.mappers.TaskRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Saves a new task into the database.
     * This method inserts a new task with its name, description, and status into the `tasks` table.
     * If the task's name or status is null or empty, no data is saved, and the method returns 0.
     *
     * @param task - The task object to be saved into the database.
     * @return int - The number of rows affected by the insert operation (typically 1 if successful, 0 if not).
     */
    public int saveTask(Task task) {
        String sql = "INSERT INTO tasks (name, description, status) VALUES (?, ?, ?)";
        if (task.getName() != null && !task.getName().isEmpty() && task.getStatus() != null) {
            return jdbcTemplate.update(sql, task.getName(), task.getDescription(), task.getStatus().name());
        } else {
            return 0;
        }

    }

    /**
     * Updates an existing task in the database.
     * This method updates the name, description, and status of the task identified by its ID in the `tasks` table.
     * If the task's name or status is null or empty, no update is performed, and the method returns 0.
     *
     * @param id - The ID of the task to update.
     * @param task - The task object containing the new values for the task to be updated.
     * @return int - The number of rows affected by the update operation (typically 1 if successful, 0 if not).
     */
    public int updateTask(long id, Task task) {
        String sql = "UPDATE tasks SET name = ?, description = ?, status = ? WHERE id = ?";
        if (task.getName() != null && !task.getName().isEmpty() && task.getStatus() != null) {
            return jdbcTemplate.update(sql, task.getName(), task.getDescription(), task.getStatus().name(), id);
        } else {
            return 0;
        }

    }

    /**
     * Finds a task in the database by its ID.
     * This method queries the database for a task with the specified ID and returns it as a `Task` object.
     * If no task is found with the given ID, it returns null.
     *
     * @param id - The ID of the task to retrieve.
     * @return task - The task object with the specified ID, or null if no task is found.
     */
    public Task findById(long id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        List<Task> tasks = jdbcTemplate.query(sql, new TaskRowMapper(), id);
        if (tasks.isEmpty()) {
            return null;
        }
        return tasks.getFirst();
    }

    /**
     * Retrieves a list of tasks from the database filtered by their status.
     * This method queries the `tasks` table for all tasks that match the specified status.
     *
     * @param status - The status to filter tasks by.
     * @return list - A list of tasks with the given status, or an empty list if no tasks match the status.
     */
    public List<Task> getTasksFilteredByStatus(Task.Status status) {
        String sql = "SELECT * FROM tasks WHERE status = ?";
        return jdbcTemplate.query(sql, new TaskRowMapper(), status.name());
    }

}
