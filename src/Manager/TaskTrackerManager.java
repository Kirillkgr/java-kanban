package Manager;

import Enums.TaskStatus;
import Models.Epic;
import Models.Task;
import Models.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskTrackerManager {
    /**
     * 1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
     */
    // 1.a. Статический HashMap для хранения задач
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
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

    public ArrayList<Task> getListOfTasks(TaskStatus status) {
        ArrayList<Task> taskByStatus = new ArrayList<>();
        switch (status) {
            case TaskStatus.NEW -> {
                for (Task task : tasks.values()) {
                    if (task.getStatus().equals(TaskStatus.NEW)) {
                        taskByStatus.add(task);
                    }
                }
            }
            case TaskStatus.IN_PROGRESS -> {
                for (Task task : tasks.values()) {
                    if (task.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                        taskByStatus.add(task);
                    }
                }
            }
            case TaskStatus.DONE -> {
                for (Task task : tasks.values()) {
                    if (task.getStatus().equals(TaskStatus.DONE)) {
                        taskByStatus.add(task);
                    }
                }
            }
        }
        return taskByStatus;
    }

    //    2.b. Удаление всех задач.
    public void removeAllTasks() {
        epicTasks.clear();
        subTasks.clear();
        tasks.clear();
    }

    public void removeAllEpicTasks() {
        removeAllTasks();
    }

    public void removeAllSubTasks() {
        for (Subtask subtask : subTasks.values()) {
            Epic epic = epicTasks.remove(subtask.getEpicId());
            epic.setSubtask(new ArrayList<>());
            epic.setStatus(TaskStatus.NEW);
            tasks.remove(subtask.getId());
        }
        subTasks.clear();
    }

    //    2.c. Получение по идентификатору.
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    public Task getSubTaskById(int id) {
        return subTasks.get(id);
    }

    //    2.d. Создание. Сам объект должен передаваться в качестве параметра.
    public void createTask(Task task) {
        tasks.put(task.getId(), task);

    }

    public Epic createEpicTask(Task task) {
        Epic epic = (Epic) task;
        epic.setId(getNewId());
        epicTasks.put(epic.getId(), epic);
        tasks.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubTask(Task task) {
        Subtask subtask = (Subtask) task;
        Epic epic = epicTasks.get(subtask.getEpicId());
        subtask.setId(getNewId());
        subtask.setEpicId(epic.getId());
        subTasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        epicTasks.put(epic.getId(), epic);
        tasks.put(task.getId(), task);
        return subtask;
    }

    //    2.e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            if (updatedTask instanceof Subtask subtask) {
                if (subTasks.containsKey(((Subtask) updatedTask).getId())) {
                    Epic epic = (Epic) epicTasks.get(subTasks.get(((Subtask) updatedTask).getEpicId()));
                    subTasks.put(subtask.getId(), subtask);
                    tasks.put(updatedTask.getId(), updatedTask);
                    epic.updateStatus();
                }
            } else if (updatedTask instanceof Epic epic) {
                epicTasks.put(epic.getId(), epic);
                tasks.put(updatedTask.getId(), updatedTask);
            }
        }
    }

    //    2.f. Удаление по идентификатору.
    public void removeTaskById(int id) {
        Task task = tasks.get(id);
        if (task instanceof Subtask) {
            Epic epic = (Epic) tasks.get(((Subtask) task).getEpicId());
            if (epic != null) {
                for (Subtask subtask : epic.getSubtasks()) {
                    if (subtask.getId() == id) {
                        subTasks.remove(subtask.getId());
                        tasks.remove(subtask.getId());
                        epic.getSubtasks().remove(subtask);
                        epicTasks.put(epic.getId(), epic);
                        tasks.put(epic.getId(), epic);
                    }
                }
                epic.updateStatus();
            }
        } else if (task instanceof Epic) {
            for (Subtask subtask : ((Epic) task).getSubtasks()) {
                subTasks.remove(subtask.getId());
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
        if (epicTasks.containsKey(epicId)) {
            subtasks.addAll(epicTasks.get(epicId).getSubtasks());
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


    // Получение всех эпиков
    public List<Epic> getAllEpics() {
        List<Epic> epicLists = new ArrayList<>();
        for (Epic epic : epicTasks.values()) {
            epic.setSubtask(new ArrayList<>());
            epicLists.add(epic);
        }
        return epicLists;
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

    public void updateSubtask(Subtask subtask) {
        Epic epic = epicTasks.get(subtask.getEpicId());
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask sb : epic.getSubtasks()) {
            if (sb.getId() == subtask.getId()) {
                subtaskList.remove(sb);
                subtaskList.add(subtask);
            } else {
                subtaskList.add(sb);
            }
        }
        epic.setSubtask(subtaskList);
        epic.updateStatus();
        tasks.put(epic.getId(), epic);
    }

    public void updateSubtaskStatus(Subtask subtask) {
        subTasks.put(subtask.getId(), subtask);
        Epic epic = epicTasks.get(subtask.getEpicId());
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask sb : epic.getSubtasks()) {
            if (sb.getId() == subtask.getId()) {
                subtaskList.add(subtask);
            } else {
                subtaskList.add(sb);
            }
        }
        epic.setSubtask(subtaskList);
        epic.updateStatus();
        tasks.put(subtask.getId(), subtask);
    }

    public void deleteSubtask(Subtask subtask) {
        Epic epic = epicTasks.get(subtask.getEpicId());
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask sb : epic.getSubtasks()) {
            if (sb.getId() != subtask.getId()) {
                subtaskList.add(sb);
            } else {
                subTasks.remove(sb.getId());
                tasks.remove(sb.getId());
            }
        }
        epic.setSubtask(subtaskList);
        tasks.put(epic.getId(), epic);
        epic.updateStatus();
    }

    public void addSubtaskToEpic(Subtask subtask) {
        if (subtask.getId() == 0) {
            subtask.setId(getNewId());
        }
        Epic epic = epicTasks.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        tasks.put(subtask.getId(), subtask);
        subTasks.put(subtask.getId(), subtask);
        epicTasks.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        tasks.put(epic.getId(), epic);
        epicTasks.put(epic.getId(), epic);
    }
}