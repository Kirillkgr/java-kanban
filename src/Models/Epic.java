package Models;

import Enums.TaskStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks;
    
    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new ArrayList<>();
    }
    
    public Epic(Integer id, String name, TaskStatus status, String description, ArrayList<Subtask> subtasks) {
        super(id, name, status, description);
        this.subtasks = subtasks;
        updateEpicTimeFields();
    }
    
    public Epic(Epic epic) {
        super(epic.getId(), epic.name, epic.description);
        ArrayList<Subtask> newSubtasks = new ArrayList<>();
        for (Subtask subtask : epic.subtasks) {
            newSubtasks.add(new Subtask(subtask));
        }
        this.subtasks = newSubtasks;
        updateEpicTimeFields();
    }
    
    // Обновляем продолжительность и время начала эпика на основе подзадач
    public void updateEpicTimeFields() {
        if (subtasks.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
            return;
        }
        
        // Находим минимальное startTime и суммируем duration
        LocalDateTime earliestStartTime = null;
        Duration totalDuration = Duration.ZERO;
        
        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null) {
                if (earliestStartTime == null || subtask.getStartTime().isBefore(earliestStartTime)) {
                    earliestStartTime = subtask.getStartTime();
                }
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }
        
        this.startTime = earliestStartTime;
        this.duration = totalDuration;
    }
    
    // Метод для вычисления времени завершения эпика
    @Override
    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }
    
    public void setSubtask(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
        updateEpicTimeFields();
    }
    
    public void addSubtask(Subtask subtask) {
        this.subtasks.add(subtask);
        updateEpicTimeFields();
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
               ", StartTime = " + startTime + "\n" +
               ", Duration = " + duration + "\n" +
               ", EndTime = " + getEndTime() + "\n" +
               "-------------------------------------\n" +
               "Subtasks = " + subtasks + "\n" +
               "#####################################\n" +
               '}';
    }
}
