package main.Service;

import main.Enums.TypeTask;
import main.Models.SubTaskModel;

import java.util.List;

public interface TaskTracker {
    void addTask(TypeTask typeTask, String name);
    void addSubTask(TypeTask typeTask, String name, List<SubTaskModel> subTasks);
    void removeTask(TypeTask typeTask);
}