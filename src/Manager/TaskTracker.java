package Manager;

import Models.Task;
import Models.Subtask;

import java.util.List;

public interface TaskTracker {
    // a. Получение списка всех задач.
    List<Task> getAllTasks();

    //    b. Удаление всех задач.
    void removeAllTasks();

    //  c. Получение по идентификатору.
    Task getTaskById(int id);

    //  d. Создание. Сам объект должен передаваться в качестве параметра.
    void addTask(Task task);

    //   e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task updatedTask);

    //   f. Удаление по идентификатору.
    void removeTaskById(int id);

    // Получение списка всех эпиков
    List<Task> getAllEpics();

    // Получение списка всех подзадач
    List<Task> getAllSubtasks();

    // Получение списка подзадач определённого эпика
    List<Subtask> getSubtasksOfEpic(int epicId);
}
