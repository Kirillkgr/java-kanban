package Manager.Impl;

import Manager.TaskManager;
import Models.Epic;
import Models.Task;
import Models.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final static HashMap<Integer, Task> tasks = new HashMap<>();
    private final static HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private final static HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private final static LinkedList<Task> historyTask = new LinkedList<>();
    private static int idCounter = 1;

    @Override
    public List<Task> getAllTasks() {
        if (tasks.isEmpty())
            return null;
        return new ArrayList<>(tasks.values());
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
        return tasks.get(id);
    }

    @Override
    public Task getEpicTaskById(int id) {
        return epicTasks.get(id);
    }

    @Override
    public Task getSubTaskById(int id) {
        return subTasks.get(id);
    }

    @Override
    public Task createTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpicTask(Epic epic) {
        epic.setId(getNewId());
        epicTasks.put(epic.getId(), epic);
        return epic;
    }

    @Override
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

    private void addViewTask(Task task) {
        historyTask.add(task);
        if (historyTask.size() > 10) {
            historyTask.remove(historyTask.getFirst());
        }
    }

    // Выдача истории с защитой от не преднамеренного изменения
    @Override
    public LinkedList<Task> getHistory() {
        LinkedList<Task> actualList = new LinkedList<>();
        for (Task task : historyTask) {
            if (task instanceof Epic epic) {
                // Обработка для Epic
                actualList.push(new Epic(epic));
            } else if (task instanceof Subtask) {
                // Обработка для Subtask
                Subtask subtask = new Subtask((Subtask) task);
                actualList.push(subtask);
            } else if (task != null) {
                // Обработка для Task
                Task actualTask = new Task(task);
                actualList.push(actualTask);
            }
        }
        return actualList;
    }


    private static int getNewId() {
        return idCounter++;
    }
}