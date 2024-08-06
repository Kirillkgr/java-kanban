package Manager;

import Enums.TaskStatus;
import Models.Epic;
import Models.Task;
import Models.Subtask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TaskTrackerManager {
    /**
     * 1. Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.
     */
    // 1.a. Статический HashMap для хранения задач
    private final static HashMap<Integer, Task> tasks = new HashMap<>();
    private final static HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private final static HashMap<Integer, Subtask> subTasks = new HashMap<>();
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
        if (tasks.isEmpty())
            return null;
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
        for (Epic epic : epicTasks.values()) {
            List<Subtask> subtaskList = epic.getSubtasks();
            for (Subtask sb : subtaskList) {
                subTasks.remove(sb.getId());
                tasks.remove(sb.getId());
            }
            tasks.remove(epic.getId());
            epicTasks.remove(epic.getId());
        }
    }

    public void removeAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epicTasks.values()) {
            epic.getSubtasks().clear();
            epic.updateStatus();
        }
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
        tasks.put(updatedTask.getId(), updatedTask);
    }

    public void updateTask(Epic updatedTask) {
        epicTasks.put(updatedTask.getId(), updatedTask);

    }

    public void updateTask(Subtask updatedTask) {
        subTasks.put(updatedTask.getId(), updatedTask);

    }

    //    2.f. Удаление по идентификатору.
    public void removeTaskById(int id) {
        if (epicTasks.containsKey(id)) {
            Epic epic = epicTasks.get(id);
            for (Subtask sb : epic.getSubtasks()) {
                subTasks.remove(sb.getId());
            }
            epicTasks.remove(epic.getId());
        } else if (subTasks.containsKey(id)) {
            Epic epic = epicTasks.get(subTasks.get(id).getEpicId());
            for (Subtask sb : epic.getSubtasks()) {
                subTasks.remove(sb.getId());
            }
        } else tasks.remove(id);
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
    public Collection<Subtask> getAllSubtasks() {
        if (subTasks.isEmpty())
            return null;
        return subTasks.values();
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
    }

    public void deleteSubtask(Subtask subtask) {
        Epic epic = epicTasks.get(subtask.getEpicId());
        ArrayList<Subtask> subtaskList = new ArrayList<>();
        for (Subtask sb : epic.getSubtasks()) {
            if (sb.getId() != subtask.getId()) {
                subtaskList.add(sb);
            } else {
                subTasks.remove(sb.getId());

            }
        }
        epic.setSubtask(subtaskList);

        epic.updateStatus();
    }

    public void addSubtaskToEpic(Subtask subtask) {
        if (subtask.getId() == 0) {
            subtask.setId(getNewId());
        }
        Epic epic = epicTasks.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        subTasks.put(subtask.getId(), subtask);
        epicTasks.put(epic.getId(), epic);
    }

    public void addTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
    }
}