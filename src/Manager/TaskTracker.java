package Manager;

import Models.Epic;
import Models.Task;
import Models.Subtask;

import java.util.HashMap;
import java.util.Map;

public class TaskTracker {
    private final Map<Integer, Task> tasks = new HashMap<>();

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
        if (task instanceof Subtask) {
            Epic epic = (Epic) tasks.get(((Subtask) task).getEpicId());
            epic.addSubtask((Subtask) task);
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }
}