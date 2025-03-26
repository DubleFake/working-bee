package com.homework.task.database.repositories;

import com.homework.task.database.templates.Task;
import com.homework.task.database.services.mappers.TaskRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Saves a new task into the database.
     * This method first retrieves the user ID from the 'users' table based on the provided username.
     * It then checks if the task name and status are provided. If so, it inserts a new task into the 'tasks' table
     * with the provided name, description, status, and the user's ID. If the task name or status is missing,
     * the task will not be saved, and the method will return false.
     *
     * @param task - The task object containing the task details (name, description, status).
     * @param username - The username of the user to whom the task is assigned.
     * @return boolean - True if any rows were affected, False if no rows were changed.
     */
    public boolean saveTask(Task task, String username) {
        String sql = "INSERT INTO tasks (name, description, status, user_id) VALUES (?, ?, ?, ?)";
        String slq2 = "SELECT id FROM users WHERE username = ?";

        long id = jdbcTemplate.queryForObject(slq2, long.class, username);

        if (task.getName() != null && !task.getName().isEmpty() && task.getStatus() != null) {
            return 0 < jdbcTemplate.update(sql, task.getName(), task.getDescription(), task.getStatus().name(), id);
        } else {
            return false;
        }

    }

    /**
     * Updates an existing task in the database.
     * This method updates the task in the 'tasks' table based on the provided task ID. It modifies the task's name, description,
     * and status if the task name and status are provided (non-null and non-empty). If the provided task name or status is missing,
     * the task will not be updated, and the method will return false. Otherwise, it returns true if the update is successful.
     *
     * @param id - The ID of the task to update.
     * @param task - The task object containing the new values for the task to be updated.
     * @return boolean - True if the task is updated successfully; false if the task is not updated due to missing name or status.
     */
    public boolean updateTask(long id, Task task) {
        String sql = "UPDATE tasks SET name = ?, description = ?, status = ? WHERE id = ?";
        if (task.getName() != null && !task.getName().isEmpty() && task.getStatus() != null) {
            return 0 < jdbcTemplate.update(sql, task.getName(), task.getDescription(), task.getStatus().name(), id);
        } else {
            return false;
        }

    }

    /**
     * Finds a task in the database by its ID.
     * This method queries the 'tasks' table and joins it with the 'users' table to find a task with the specified task ID
     * and the username of the user to whom the task is assigned. If the task is found, it returns the task object. If no
     * matching task is found, it returns null.
     *
     * @param id - The ID of the task to retrieve.
     * @param username - The username of the user associated with the task.
     * @return task - The task object if found, or null if no task matches the given ID and username.
     */
    public Task findById(long id, String username) {
        String sql = "SELECT * FROM tasks AS t LEFT JOIN users AS u ON t.user_id = u.id WHERE t.id = ? AND u.username = ?";
        List<Task> tasks = jdbcTemplate.query(sql, new TaskRowMapper(), id, username);
        if (tasks.isEmpty()) {
            return null;
        }
        return tasks.getFirst();
    }

    /**
     * Retrieves a list of tasks from the database filtered by their status.
     * This method queries the 'tasks' table and joins it with the 'users' table to find all tasks that have the specified
     * status and are assigned to the user with the given username. It returns a list of tasks that match the criteria.
     *
     * @param status - The status of the tasks to be retrieved.
     * @param username - The username of the user associated with the tasks.
     * @return list<task> - A list of tasks that match the specified status and username. If no tasks match, an empty list is returned.
     */
    public List<Task> getTasksFilteredByStatus(Task.Status status, String username) {
        String sql = "SELECT * FROM tasks AS t LEFT JOIN users AS u ON t.user_id = u.id WHERE status = ? AND u.username = ?";
        return jdbcTemplate.query(sql, new TaskRowMapper(), status.name(), username);
    }

}
