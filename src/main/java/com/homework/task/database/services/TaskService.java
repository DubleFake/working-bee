package com.homework.task.database.services;

import com.homework.task.database.templates.Task;
import com.homework.task.database.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Saves a new task in the repository.
     *
     * @param task - The task object to be saved.
     * @return boolean - Execution result (true if successful, false if not).
     */
    public boolean saveTask(Task task, String username) {
        return taskRepository.saveTask(task, username);
    }

    /**
     * Finds a task by its ID.
     *
     * @param id - The ID of the task to find.
     * @return Task - The task object with the given ID, or null if no task is found.
     */
    public Task findById(long id, String username) {
        return taskRepository.findById(id, username);
    }

    /**
     * Updates an existing task in the repository with new values.
     * If the task exists, its fields (status, name, description) will be updated with those from the new task.
     * If the task does not exist, no changes will be made, and the method will return false.
     *
     * @param id - The ID of the task to update.
     * @param newTask - The task object containing the new data to update the existing task.
     * @return boolean - Execution result (true if the update is successful, false if the task is not found).
     */
    public boolean updateTask(long id, Task newTask, String username) {
        Task task = taskRepository.findById(id, username);
        if (task != null) {
            task.setStatus(newTask.getStatus());
            task.setName(newTask.getName());
            task.setDescription(newTask.getDescription());
            return taskRepository.updateTask(id, task);  // Save the updated record
        } else {
            return false;
        }
    }

    /**
     * Retrieves a list of tasks that match a specific status.
     *
     * @param status - The status to filter tasks by.
     * @return list - A list of tasks that have the given status, or an empty list if no tasks match.
     */
    public List<Task> getTasksFilteredByStatus(Task.Status status, String username) {
        return taskRepository.getTasksFilteredByStatus(status, username);
    }

}
