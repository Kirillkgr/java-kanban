package Manager.Impl;

import Manager.TaskTrackerManager;
import Models.Epic;
import Models.Task;
import Models.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskTrackerManager {

    private final static HashMap<Integer, Task> tasks = new HashMap<>();
    private final static HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private final static HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private static int idCounter = 1;


    public List<Task> getAllTasks() {
        if (tasks.isEmpty())
            return null;
        return new ArrayList<>(tasks.values());
    }


    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpicTasks() {
        subTasks.clear();
        epicTasks.clear();
    }

    public void removeAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epicTasks.values()) {
            epic.getSubtasks().clear();
            epic.updateStatus();
        }
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    public Task getSubTaskById(int id) {
        return subTasks.get(id);
    }

    public Task createTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpicTask(Epic epic) {
        epic.setId(getNewId());
        epicTasks.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubTask(Subtask subtask) {
        Epic epic = epicTasks.get(subtask.getEpicId());
        subtask.setId(getNewId());
        subtask.setEpicId(epic.getId());
        epic.addSubtask(subtask);
        subTasks.put(subtask.getId(), subtask);
        epic.updateStatus();
        epicTasks.put(epic.getId(), epic);
        return subtask;
    }

    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
    }

    public void updateEpic(Epic epic) {
        epicTasks.put(epic.getId(), epic);

    }

    public void removeEpicById(int id) {
        if (epicTasks.containsKey(id)) {
            Epic epic = epicTasks.get(id);
            for (Subtask sb : epic.getSubtasks()) {
                subTasks.remove(sb.getId());
            }
        }
    }

    public void removeSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            Epic epic = epicTasks.get(subTasks.get(id).getEpicId());
            ArrayList<Subtask> newSubtasksForEpic = new ArrayList<>();
            for (Subtask sb : epic.getSubtasks()) {
                if (sb.getId() != id) {
                    newSubtasksForEpic.add(sb);
                } else {
                    subTasks.remove(id);
                }
            }
            epic.setSubtask(newSubtasksForEpic);
            epic.updateStatus();
        }
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public List<Subtask> getSubtasksOfEpic(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();
        if (epicTasks.containsKey(epicId)) {
            subtasks.addAll(epicTasks.get(epicId).getSubtasks());
        }
        return subtasks;
    }

    public List<Epic> getAllEpics() {
        List<Epic> epicLists = new ArrayList<>();
        for (Epic epic : epicTasks.values()) {
            epic.setSubtask(new ArrayList<>());
            epicLists.add(epic);
        }
        return epicLists;
    }

    public List<Subtask> getAllSubtasks() {
        return subTasks.values().stream().toList();
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

    private static int getNewId() {
        return idCounter++;
    }
}