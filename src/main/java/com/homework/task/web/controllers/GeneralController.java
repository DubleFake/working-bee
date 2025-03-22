package com.homework.task.web.controllers;

import com.homework.task.database.Task;
import com.homework.task.database.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GeneralController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/tasks")
    public ResponseEntity<String> saveTask(@RequestBody Task task) {
        if(taskService.saveTask(task) == 1) {
            return new ResponseEntity<>("Created.", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Forbidden.", HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/tasks/{id}")
    public String editTask(@PathVariable int id) {
        return "null";
    }

    @GetMapping("/tasks/{id}")
    public String getTask(@PathVariable int id) {
        return "null";
    }

    @GetMapping("/tasks?status={status}")
    public String getFilteredTasksByStatus(@PathVariable int id) {
        return "null";
    }
}
