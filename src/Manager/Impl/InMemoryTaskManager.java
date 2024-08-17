package Manager.Impl;

import Manager.HistoryManager;
import Manager.TaskManager;
import Models.Epic;
import Models.Task;
import Models.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private static HashMap<Integer, Task> tasks;
    private static HashMap<Integer, Epic> epicTasks;
    private static HashMap<Integer, Subtask> subTasks;
    private static int idCounter = 1;
    private static HistoryManager historyManager;

    public InMemoryTaskManager() {
        historyManager = new InMemoryHistoryManager();
        tasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
    }

    @Override
    public List<Task> getAllTasks() {
        return tasks.values().stream().toList();
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllEpicTasks() {
        subTasks.clear();
        epicTasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epicTasks.values()) {
            epic.getSubtasks().clear();
            epic.updateStatus();
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.addViewTask(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task getEpicTaskById(int id) {
        Epic epic = epicTasks.get(id);
        historyManager.addViewTask(epic);
        return epic;
    }

    @Override
    public Task getSubTaskById(int id) {
        Subtask subtask = subTasks.get(id);
        historyManager.addViewTask(subtask);
        return subtask;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpicTask(Epic epic) {
        if (epic == null || !(epic instanceof Epic)) {
            return null;
        }
        epic.setId(getNewId());
        epicTasks.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        if (!(subtask instanceof Subtask) || epicTasks.get(subtask.getEpicId()) == null) {
            return null;
        }
        Epic epic = epicTasks.get(subtask.getEpicId());
        subtask.setId(getNewId());
        subtask.setEpicId(epic.getId());
        epic.addSubtask(subtask);
        subTasks.put(subtask.getId(), subtask);
        epic.updateStatus();
        epicTasks.put(epic.getId(), epic);
        return subtask;
    }

    @Override
    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicTasks.put(epic.getId(), epic);

    }

    @Override
    public void removeEpicById(int id) {
        if (epicTasks.containsKey(id)) {
            Epic epic = epicTasks.get(id);
            for (Subtask sb : epic.getSubtasks()) {
                subTasks.remove(sb.getId());
            }
        }
    }

    @Override
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

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        List<Subtask> subtasks = new ArrayList<>();
        if (epicTasks.containsKey(epicId)) {
            subtasks.addAll(epicTasks.get(epicId).getSubtasks());
        }
        return subtasks;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> epicLists = new ArrayList<>();
        for (Epic epic : epicTasks.values()) {
            epic.setSubtask(new ArrayList<>());
            epicLists.add(epic);
        }
        return epicLists;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return subTasks.values().stream().toList();
    }

    @Override
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