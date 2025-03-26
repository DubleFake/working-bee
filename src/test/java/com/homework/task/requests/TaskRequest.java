package com.homework.task.requests;

import com.homework.task.database.templates.Task;

public class TaskRequest {
    private Task.Status status;
    private String name;
    private String description;

    public TaskRequest() {
    }

    public TaskRequest(Task.Status status, String name, String description) {
        this.status = status;
        this.name = name;
        this.description = description;
    }

    public Task.Status getStatus() {
        return status;
    }

    public void setStatus(Task.Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
