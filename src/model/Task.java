package model;

import java.util.Objects;

public class Task {

    protected String name;
    protected String description;

    protected int id;
    protected Status status;
    protected TaskType taskType;


    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, TaskType taskType, String name, Status status, String description) {
        this.id = id;
        this.taskType = taskType;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public Integer getEpicId() {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && taskType == task.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, taskType);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}

