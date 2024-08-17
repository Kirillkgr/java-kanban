package Manager.Impl;

import Manager.HistoryManager;
import Models.Epic;
import Models.Subtask;
import Models.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private static LinkedList<Task> historyTask;

    public InMemoryHistoryManager() {
        historyTask = new LinkedList<>();
    }

    public void addViewTask(Task task) {
        if (task instanceof Epic epic) {
            // Обработка для Epic
            historyTask.push(new Epic(epic));
        } else if (task instanceof Subtask) {
            // Обработка для Subtask
            Subtask subtask = new Subtask((Subtask) task);
            historyTask.push(subtask);
        } else if (task != null) {
            // Обработка для Task
            Task actualTask = new Task(task);
            historyTask.push(actualTask);
        }
        if (historyTask.size() > 10) {
            historyTask.remove(historyTask.getFirst());
        }
    }

    // Выдача истории с защитой от не преднамеренного изменения
    public LinkedList<Task> getHistory() {
        LinkedList<Task> actualList = new LinkedList<>();
        for (Task task : historyTask) {
            if (task instanceof Epic epic) {
                // Обработка для Epic
                actualList.push(new Epic(epic));
            } else if (task instanceof Subtask) {
                // Обработка для Subtask
                Subtask subtask = new Subtask((Subtask) task);
                actualList.push(subtask);
            } else if (task != null) {
                // Обработка для Task
                Task actualTask = new Task(task);
                actualList.push(actualTask);
            }
        }
        return actualList;
    }
}
