package Manager;

import Models.Task;

import java.util.LinkedList;

public interface HistoryManager {

    void addViewTask(Task task);

    LinkedList<Task> getHistory();
}
