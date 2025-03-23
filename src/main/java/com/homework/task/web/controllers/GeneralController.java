package com.homework.task.web.controllers;

import com.homework.task.database.Task;
import com.homework.task.database.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GeneralController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/tasks")
    public ResponseEntity<String> saveTask(@RequestBody Task task) {
        if(taskService.saveTask(task) == 1) {
            return new ResponseEntity<>("Created.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Bad request.", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<String> editTask(@PathVariable long id, @RequestBody Task task) {
        if(taskService.updateTask(id, task) == 1) {
            return new ResponseEntity<>("OK.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Forbidden.", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTask(@PathVariable long id) {
        Task task = taskService.findById(id);
        if (task == null) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } else {
            return new ResponseEntity<>(taskService.findById(id), HttpStatus.OK);
        }
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getFilteredTasksByStatus(@RequestParam Task.Status status) {
        return new ResponseEntity<>(taskService.getTasksFilteredByStatus(status), HttpStatus.OK);
    }
}
