package com.homework.task.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public int saveTask(Task task) {
        return taskRepository.saveTask(task);
    }

}
