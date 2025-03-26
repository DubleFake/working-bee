package com.homework.task.web.controllers;

import com.homework.task.database.templates.Task;
import com.homework.task.database.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Retrieves the username of the currently authenticated user.
     * This method extracts the username of the currently authenticated user from the SecurityContext.
     * It checks if the principal is an instance of `UserDetails` to obtain the username; if not, it falls back
     * to calling `toString()` on the principal.
     *
     * @return String - The username of the authenticated user.
     */

    private String getPrincipalUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username;

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = authentication.getPrincipal().toString();
        }
        return username;
    }

    /**
     * Creates a new task and saves it to the database.
     * This method attempts to save a new task to the database. The task is saved using the `taskService.saveTask` method,
     * and the authenticated user's username is automatically passed along to associate the task with the current user.
     * If the task is successfully created, the response will be HTTP 201 (Created). If there is an issue with the request,
     * it returns HTTP 400 (Bad Request).
     *
     * @param task - The task object to be saved.
     * @return ResponseEntity - A response entity with a message indicating the success or failure of the operation.
     */

    @PostMapping("/tasks")
    public ResponseEntity<String> saveTask(@RequestBody Task task) {

        if(taskService.saveTask(task, getPrincipalUsername())) {
            return new ResponseEntity<>("Created.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Bad request.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates an existing task in the database.
     * This method attempts to update an existing task with the specified ID. The update is performed using the `taskService.updateTask` method,
     * and the authenticated user's username is automatically passed to ensure that the task is updated for the correct user.
     * If the task is successfully updated, the response will be HTTP 200 (OK). If the user is not authorized to edit the task,
     * it returns HTTP 403 (Forbidden).
     *
     * @param id - The ID of the task to be updated.
     * @param task - The task object containing the updated details.
     * @return ResponseEntity - A response entity with a message indicating the success or failure of the operation.
     */

    @PutMapping("/tasks/{id}")
    public ResponseEntity<String> editTask(@PathVariable long id, @RequestBody Task task) {

        if(taskService.updateTask(id, task, getPrincipalUsername())) {
            return new ResponseEntity<>("OK.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Forbidden.", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Retrieves a task by its ID.
     * This method retrieves a task with the specified ID from the database. The task is fetched using the `taskService.findById` method,
     * and the username of the authenticated user is passed to ensure that the task belongs to the correct user.
     * If the task is found, it is returned with HTTP 200 (OK). If the task does not exist or the user is not authorized,
     * it returns HTTP 403 (Forbidden).
     *
     * @param id - The ID of the task to be retrieved.
     * @return ResponseEntity - A response entity containing the task object and an appropriate HTTP status code.
     */

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTask(@PathVariable long id) {

        String username = getPrincipalUsername();

        Task task = taskService.findById(id, username);
        if (task == null) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(taskService.findById(id, username), HttpStatus.OK);
        }
    }

    /**
     * Retrieves a list of tasks filtered by status.
     * This method fetches a list of tasks with a specified status, filtered based on the authenticated user's username.
     * The status is passed as a request parameter, and the tasks are fetched using the `taskService.getTasksFilteredByStatus` method.
     * The list of tasks is returned with HTTP 200 (OK).
     *
     * @param status - The status of the tasks to be retrieved.
     * @return ResponseEntity - A response entity containing a list of tasks and HTTP status code 200 (OK).
     */

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getFilteredTasksByStatus(@RequestParam Task.Status status) {
        return new ResponseEntity<>(taskService.getTasksFilteredByStatus(status, getPrincipalUsername()), HttpStatus.OK);
    }
}
