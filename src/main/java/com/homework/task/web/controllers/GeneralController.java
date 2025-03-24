package com.homework.task.web.controllers;

import com.homework.task.database.templates.Task;
import com.homework.task.database.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GeneralController {

    @Autowired
    private TaskService taskService;

    /**
     * Creates a new task.
     * This method maps to the HTTP POST request at `/tasks`. It receives a task object in the request body and attempts to save it.
     * If the task is successfully created, it returns a response with status code 201 (Created).
     * If there is an issue with the task (e.g., invalid data), it returns a response with status code 400 (Bad Request).
     *
     * @param task - The task object to be saved, passed in the request body.
     * @return responseEntity - A ResponseEntity containing the result message and appropriate HTTP status code.
     */
    @PostMapping("/tasks")
    public ResponseEntity<String> saveTask(@RequestBody Task task) {
        if(taskService.saveTask(task) == 1) {
            return new ResponseEntity<>("Created.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Bad request.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Edits an existing task.
     * This method maps to the HTTP PUT request at `/tasks/{id}`. It receives the task ID as a path variable and a task object in the request body.
     * If the task is successfully updated, it returns a response with status code 200 (OK).
     * If there is an issue (e.g., task not found or invalid data), it returns a response with status code 403 (Forbidden).
     *
     * @param id - The ID of the task to update, passed as a path variable.
     * @param task - The task object containing the updated data, passed in the request body.
     * @return responseEntity - A ResponseEntity containing the result message and appropriate HTTP status code.
     */
    @PutMapping("/tasks/{id}")
    public ResponseEntity<String> editTask(@PathVariable long id, @RequestBody Task task) {
        if(taskService.updateTask(id, task) == 1) {
            return new ResponseEntity<>("OK.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Forbidden.", HttpStatus.FORBIDDEN);
        }
    }
/**
 * Retrieves a task by its ID.
 * This method maps to the HTTP GET request at `/tasks/{id}`. It receives the task ID as a path variable and returns the task object.
 * If the task with the specified ID is found, it returns the task with a status code of 200 (OK).
 * If the task is not found, it returns a response with status code 403 (Forbidden).
 *
 * @param id - The ID of the task to retrieve, passed as a path variable.
 * @return responseEntity - A ResponseEntity containing the task object and an HTTP status code.
 */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTask(@PathVariable long id) {
        Task task = taskService.findById(id);
        if (task == null) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(taskService.findById(id), HttpStatus.OK);
        }
    }

    /**
     * Retrieves tasks filtered by their status.
     * This method maps to the HTTP GET request at `/tasks`. It receives the status as a request parameter and returns a list of tasks.
     * If tasks with the given status are found, it returns them with a status code of 200 (OK).
     *
     * @param status - The status to filter tasks by, passed as a query parameter.
     * @return responseEntity - A ResponseEntity containing a list of tasks with the specified status and HTTP status code 200 (OK).
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getFilteredTasksByStatus(@RequestParam Task.Status status) {
        return new ResponseEntity<>(taskService.getTasksFilteredByStatus(status), HttpStatus.OK);
    }
}
