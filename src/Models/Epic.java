package Models;

import Enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;

// Класс для эпиков
public class Epic extends Task {
    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
    }
    public Epic(Integer id, String name, TaskStatus status, String description,ArrayList<Subtask> subtasks) {
        super(id, name, status, description);
        this.subtasks = subtasks;
    }

    public Epic(Epic epic) {
        super(epic.getId(), epic.name, epic.description);
        ArrayList<Subtask> newSubtasks = new ArrayList<>();
        for (Subtask subtask : epic.subtasks) {
            newSubtasks.add(new Subtask(subtask));
        }
        this.subtasks = newSubtasks;
    }

    public void setSubtask(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        this.subtasks.add(subtask);
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            this.status = TaskStatus.NEW;
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                this.status = TaskStatus.IN_PROGRESS;
                return;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }
        if (allNew) {
            this.status = TaskStatus.NEW;
        } else if (allDone) {
            this.status = TaskStatus.DONE;
        } else {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }

    public ArrayList<Subtask> getSubtasks() {
        return this.subtasks;
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        if (status == TaskStatus.DONE) {
            for (Subtask subtask : subtasks) {
                subtask.setStatus(TaskStatus.DONE);
            }
            this.status = TaskStatus.DONE;
        }
    }

    @Override
    public String toString() {
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