package Manager;

import Manager.Impl.InMemoryTaskManager;

public class Managers {

    TaskManager getDefaultInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }
}
