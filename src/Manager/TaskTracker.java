package Manager;

import Models.Epic;
import Models.Task;
import Models.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskTracker {
    /**
     * 1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
     */
    // 1.a. Статический HashMap для хранения задач
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private static int idCounter = 1;

    /**
     * Получение нового индификатора
     */
    public static int getNewId() {
        return idCounter++;
    }

    /**
     * 2. Методы для каждого из типа задач(Задача/Эпик/Подзадача):
     */
    //    2.a. Получение списка всех задач.
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    //    2.b. Удаление всех задач.
    public void removeAllTasks() {
        tasks.clear();
    }

    //    2.c. Получение по идентификатору.
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    //    2.d. Создание. Сам объект должен передаваться в качестве параметра.
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
        if (task instanceof Subtask) {
            Epic epic = (Epic) tasks.get(((Subtask) task).getEpicId());
            epic.addSubtask((Subtask) task);
            epic.updateStatus();
        }
    }

    //    2.e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
            if (updatedTask instanceof Subtask) {
                Epic epic = (Epic) tasks.get(((Subtask) updatedTask).getEpicId());
                epic.updateStatus();
            } else if (updatedTask instanceof Epic) {
                ((Epic) updatedTask).updateStatus();
            }
        }
    }

    //    2.f. Удаление по идентификатору.
    public void removeTaskById(int id) {
        Task task = tasks.remove(id);
        if (task instanceof Subtask) {
            Epic epic = (Epic) tasks.get(((Subtask) task).getEpicId());
            if (epic != null) {
                epic.getSubtasks().remove(task);
                epic.updateStatus();
            }
        } else if (task instanceof Epic) {
            for (Subtask subtask : ((Epic) task).getSubtasks()) {
                tasks.remove(subtask.getId());
            }
        }
    }

    /**
     * 3. Дополнительные методы:
     */
    //  3.a. Получение списка всех подзадач определённого эпика.
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();
        Task task = tasks.get(epicId);
        if (task instanceof Epic) {
            subtasks.addAll(((Epic) task).getSubtasks());
        }
        return subtasks;
    }

    /**
     * 4. Дополнительные методы:
     */
    // 4.a. Менеджер сам не выбирает статус для задачи.
    //          Информация о нём приходит менеджеру вместе
    //          с информацией о самой задаче. По этим данным
    //          в одних случаях он будет сохранять статус, в
    //          других будет рассчитывать.
    // 4.b. Для эпиков:
    //          если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
    //          если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
    //          во всех остальных случаях статус должен быть IN_PROGRESS.
    public void updateTaskStatus(Task task) {
        if (task instanceof Subtask) {
            Epic epic = (Epic) tasks.get(((Subtask) task).getEpicId());
            epic.updateStatus();
        }
        System.out.println("Задача epic обновляется автоматически");
    }

    // Получение всех эпиков
    public List<Task> getAllEpics() {
        List<Task> epics = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic) {
                epics.add(task);
            }
        }
        return epics;
    }

    // Получение всех подзадач
    public List<Task> getAllSubtasks() {
        List<Task> subtasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) {
                subtasks.add(task);
            }
        }
        return subtasks;
    }
}