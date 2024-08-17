package Manager;

import Manager.Impl.InMemoryHistoryManager;
import Manager.Impl.InMemoryTaskManager;

public class Managers {

    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
