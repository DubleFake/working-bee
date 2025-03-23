package com.homework.task.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public int saveTask(Task task) {
        return taskRepository.saveTask(task);
    }

    public Task findById(long id) {
        return taskRepository.findById(id);
    }

    public int updateTask(long id, Task newTask) {
        Task task = taskRepository.findById(id);
        if (task != null) {
            task.setStatus(newTask.getStatus());
            task.setName(newTask.getName());
            task.setDescription(newTask.getDescription());
            return taskRepository.saveTask(task);  // Save the updated record
        } else {
            return 0;
        }
    }

    public List<Task> getTasksFilteredByStatus(Task.Status status) {
        return taskRepository.getTasksFilteredByStatus(status);
    }

}
