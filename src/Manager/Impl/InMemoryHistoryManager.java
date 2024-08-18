package Manager.Impl;

import Manager.HistoryManager;
import Models.Epic;
import Models.Subtask;
import Models.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> historyTask;

    public InMemoryHistoryManager() {
        historyTask = new LinkedList<>();
    }

    public void addViewTask(Task task) {
        historyTask.push(task);
        if (historyTask.size() > 10) {
            historyTask.remove(historyTask.getFirst());
        }
    }

    public LinkedList<Task> getHistory() {
        return historyTask;
    }
}
