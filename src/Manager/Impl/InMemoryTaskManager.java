package Manager.Impl;

import Manager.HistoryManager;
import Manager.TaskManager;
import Models.Epic;
import Models.Subtask;
import Models.Task;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
	
	private HashMap<Integer, Task> tasks;
	private HashMap<Integer, Epic> epicTasks;
	private HashMap<Integer, Subtask> subTasks;
	private int idCounter = 1;
	private HistoryManager historyManager;
	
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
		historyManager.add(tasks.get(id));
		return tasks.get(id);
	}
	
	@Override
	public Task getEpicTaskById(int id) {
		Epic epic = epicTasks.get(id);
		historyManager.add(epic);
		return epic;
	}
	
	@Override
	public Task getSubTaskById(int id) {
		Subtask subtask = subTasks.get(id);
		historyManager.add(subtask);
		return subtask;
	}
	
	@Override
	public void createTask(Task task) {
		task.setId(getNewId());
		tasks.put(task.getId(), task);
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
			if (Objects.equals(sb.getId(), subtask.getId())) {
				subtaskList.remove(sb);
				subtaskList.add(subtask);
			} else {
				subtaskList.add(sb);
			}
		}
		epic.setSubtask(subtaskList);
		epic.updateStatus();
	}
	
	@Override
	public List<Task> getHistory() {
		return historyManager.getHistory();
	}
	
	@Override
	public boolean isSubtaskTimeIntersect(Subtask newSubtask) {
		Epic epic = getEpicTasks().get(newSubtask.getEpicId());
		if (epic != null && isTimeOverlap(newSubtask, epic)) {
			return true;
		}
		return getSubTasks().values().stream()
				.filter(subtask -> !subtask.getId().equals(newSubtask.getId()) && subtask.getEpicId() == newSubtask.getEpicId())
				.anyMatch(subtask -> isTimeOverlap(newSubtask, subtask));
	}
	
	@Override
	public boolean isTaskTimeIntersect(Task newTask) {
		return getTasks().values().stream()
				.filter(existingTask -> !existingTask.getId().equals(newTask.getId())) // исключаем новую задачу
				.anyMatch(existingTask -> isTimeOverlap(newTask, existingTask));
	}
	
	@Override
	public boolean isTimeOverlap(Task task1, Task task2) {
		LocalDateTime start1 = task1.getStartTime();
		LocalDateTime end1 = task1.getEndTime();
		LocalDateTime start2 = task2.getStartTime();
		LocalDateTime end2 = task2.getEndTime();
		
		// Проверяем пересечения временных интервалов
		return start1.isBefore(end2) && start2.isBefore(end1);
	}
	
	
	public HashMap<Integer, Task> getTasks() {
		return tasks;
	}
	
	public HashMap<Integer, Subtask> getSubTasks() {
		return subTasks;
	}
	
	public HashMap<Integer, Epic> getEpicTasks() {
		return epicTasks;
	}
	
	public HistoryManager getHistoryManager() {
		return historyManager;
	}
	
	public void setHistoryManager(HistoryManager historyManager) {
		this.historyManager = historyManager;
	}
	
	public void setTasks(HashMap<Integer, Task> tasks) {
		this.tasks = tasks;
	}
	
	public void setEpicTasks(HashMap<Integer, Epic> epicTasks) {
		this.epicTasks = epicTasks;
	}
	
	public void setSubTasks(HashMap<Integer, Subtask> subTasks) {
		this.subTasks = subTasks;
	}
	
	private int getNewId() {
		return idCounter++;
	}
}