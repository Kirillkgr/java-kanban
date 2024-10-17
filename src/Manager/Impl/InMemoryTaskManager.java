package Manager.Impl;

import Manager.HistoryManager;
import Manager.TaskManager;
import Models.Epic;
import Models.Task;
import Models.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;

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
	public ArrayList<Task> getAllTasks() {
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
	public Optional<Task> getTaskById(int id) {
		historyManager.add(tasks.get(id));
		return Optional.ofNullable(tasks.get(id));
	}
	
	@Override
	public Task getEpicTaskById(int id) {
		Epic epic = epicTasks.get(id);
		historyManager.add(epic);
		return epic;
	}
	
	@Override
	public Optional<Subtask> getSubTaskById(int id) {
		Subtask subtask = subTasks.get(id);
		historyManager.add(subtask);
		return Optional.ofNullable(subtask);
	}
	
	@Override
	public int createTask(Task task) {
		task.setId(getNewId());
		tasks.put(task.getId(), task);
		return task.getId();
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
	public boolean createSubTask(Subtask subtask) {
		if (!isTaskTimeIntersect(subtask)) {
			if (!(subtask instanceof Subtask) || epicTasks.get(subtask.getEpicId()) == null) {
				return false;
			}
			Epic epic = epicTasks.get(subtask.getEpicId());
			subtask.setId(getNewId());
			subtask.setEpicId(epic.getId());
			epic.addSubtask(subtask);
			subTasks.put(subtask.getId(), subtask);
			epic.updateStatus();
			epicTasks.put(epic.getId(), epic);
			return true;
		}
		return false;
	}
	
	@Override
	public Task updateTask(Task updatedTask) {
		tasks.put(updatedTask.getId(), updatedTask);
		return tasks.get(updatedTask.getId());
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
	public boolean removeSubTaskById(int id) {
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
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeTaskById(int id) {
		tasks.remove(id);
		return !tasks.containsKey(id);
		
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
	public boolean updateSubtask(Subtask subtask) {
		if (!isTaskTimeIntersect(subtask)) {
			Epic epic = epicTasks.get(subtask.getEpicId());
			long totalTimeForSubtaskInMinutes = 0;
			LocalDateTime startTime = subtask.getStartTime();
			ArrayList<Subtask> subtaskList = new ArrayList<>();
			for (Subtask sb : epic.getSubtasks()) {
				if (Objects.equals(sb.getId(), subtask.getId())) {
					subtaskList.remove(sb);
					subtaskList.add(subtask);
				} else {
					subtaskList.add(sb);
				}
				if (sb.getDuration() != null) {
					totalTimeForSubtaskInMinutes += (sb.getDuration().getSeconds() / 60);
				}
				if (sb.getDuration() == null || sb.getDuration().getSeconds() < 0) {
					sb.setDuration(Duration.ofSeconds(0));
				}
				if (startTime != null && startTime.isAfter(sb.getStartTime())) {
					startTime = sb.getStartTime();//⌛
				}
			}
			if (epic.getDuration() == null || epic.getDuration().getSeconds() > 0) {
				epic.setDuration(Duration.ofSeconds(0));
			}
			epic.setSubtask(subtaskList);
			epic.setDuration(epic.getDuration().plusMinutes(totalTimeForSubtaskInMinutes));
			epic.updateStatus();
			return true;
		}
		return false;
	}
	
	@Override
	public List<Task> getHistory() {
		return historyManager.getHistory();
	}
	
	@Override
	public boolean isSubtaskTimeIntersect(Subtask newSubtask) {
		return false;
	}
	
	@Override
	public boolean isTaskTimeIntersect(Task newTask) {
		return isTaskOverlap(newTask);
	}
	
	public TreeSet<Task> getPrioritizedTasks() {
		return prioritizedTasks;
	}
	
	public boolean isTaskOverlap(Task newTask) {
		if (newTask.getStartTime() == null ||
			newTask.getDuration() == null)
			return false;
		
		for (Task existingTask : getPrioritizedTasks()) {
			if ((existingTask.getStartTime() != null || existingTask.getDuration() != null) &&
				(newTask.getStartTime().isBefore(existingTask.getEndTime()) && newTask.getEndTime().isAfter(existingTask.getStartTime()))) {
				return true;
			}
		}
		
		return false; // Если пересечений не найдено, возвращаем false
	}
	
	
	@Override
	public boolean isTimeOverlap(Task task1, Task task2) {
		return false;
	}
	
	
	private int getNewId() {
		return idCounter++;
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
	
	private final TreeSet<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
		if (task1.getStartTime() == null && task2.getStartTime() == null) {
			return Integer.compare(task1.getId(), task2.getId()); // Сравниваем по ID, если у обеих задач нет startTime
		}
		if (task1.getStartTime() == null) {
			return 1;
		}
		if (task2.getStartTime() == null) {
			return -1;
		}
		return task1.getStartTime().compareTo(task2.getStartTime());
	});
}