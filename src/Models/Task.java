package Models;

import Enums.TaskStatus;

public class Task {
    private static int idCounter = 0;
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;

    public Task(String name, String description) {
        this.id = ++idCounter;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}