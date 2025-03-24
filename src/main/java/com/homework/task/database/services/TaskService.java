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
     * @return int - The number of rows affected (usually 1 if successful, 0 if not).
     */
    public int saveTask(Task task) {
        return taskRepository.saveTask(task);
    }

    /**
     * Finds a task by its ID.
     *
     * @param id - The ID of the task to find.
     * @return Task - The task object with the given ID, or null if no task is found.
     */
    public Task findById(long id) {
        return taskRepository.findById(id);
    }

    /**
     * Updates an existing task in the repository with new values.
     * If the task exists, its fields (status, name, description) will be updated with those from the new task.
     * If the task does not exist, no changes will be made, and the method will return 0.
     *
     * @param id - The ID of the task to update.
     * @param newTask - The task object containing the new data to update the existing task.
     * @return int - The number of rows affected (usually 1 if the update is successful, 0 if the task is not found).
     */
    public int updateTask(long id, Task newTask) {
        Task task = taskRepository.findById(id);
        if (task != null) {
            task.setStatus(newTask.getStatus());
            task.setName(newTask.getName());
            task.setDescription(newTask.getDescription());
            return taskRepository.updateTask(id, task);  // Save the updated record
        } else {
            return 0;
        }
    }

    /**
     * Retrieves a list of tasks that match a specific status.
     *
     * @param status - The status to filter tasks by.
     * @return list - A list of tasks that have the given status, or an empty list if no tasks match.
     */
    public List<Task> getTasksFilteredByStatus(Task.Status status) {
        return taskRepository.getTasksFilteredByStatus(status);
    }

}
