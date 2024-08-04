package Models;

import Enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;

// Класс для эпиков
public class Epic extends Task {
    private final List<Subtask> subtasks;

    public Epic(Integer id,String name, String description) {
        super(id,name, description);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void updateStatus() {
        boolean allDone = true;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
                break;
            }
        }
        this.status = allDone ? TaskStatus.DONE : TaskStatus.IN_PROGRESS;
    }

    public List<Subtask> getSubtasks() {
        return this.subtasks;
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        if (status == TaskStatus.DONE) {
            for (Subtask subtask : subtasks) {
                subtask.setStatus(TaskStatus.DONE);
            }
        }
    }

    @Override
    public String toString() {
        this.updateStatus();
        return "#####################################\n" +
                "Epic { " +
                "Id = " + id + "\n" +
                ", Name = " + name + "\n" +
                ", status = " + status + "\n" +
                ", Description = " + description + "\n" +
                "-------------------------------------\n" +
                " \n Subtasks=" + subtasks + "\n" +
                "#####################################\n" +
                '}';
    }
}