package Manager;

import Models.Epic;
import Models.Task;
import Models.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskTracker {
    //Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
    private final Map<Integer, Task> tasks = new HashMap<>();


    // a. Получение списка всех задач.
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    //    b. Удаление всех задач.
    public void removeAllTasks() {
        tasks.clear();
    }

    //  c. Получение по идентификатору.
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    //  d. Создание. Сам объект должен передаваться в качестве параметра.
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
        if (task instanceof Subtask) {
            Epic epic = (Epic) tasks.get(((Subtask) task).getEpicId());
            epic.addSubtask((Subtask) task);
            epic.updateStatus();
        }
    }

    //   e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
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

    //   f. Удаление по идентификатору.
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


    // Получение списка всех эпиков
    public List<Task> getAllEpics() {
        List<Task> epics = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Epic) {
                epics.add(task);
            }
        }
        return epics;
    }

    // Получение списка всех подзадач
    public List<Task> getAllSubtasks() {
        List<Task> subtasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task instanceof Subtask) {
                subtasks.add(task);
            }
        }
        return subtasks;
    }


    // Получение списка подзадач определённого эпика
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();
        Task task = tasks.get(epicId);
        if (task instanceof Epic) {
            subtasks.addAll(((Epic) task).getSubtasks());
        }
        return subtasks;
    }
}