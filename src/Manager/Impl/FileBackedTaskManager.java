package Manager.Impl;

import Enums.TaskStatus;
import Enums.TypeTask;
import Models.Epic;
import Models.Subtask;
import Models.Task;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {
	private final String pathToFile = System.getProperty("user.home") + File.separator + "tasks.csv";
	
	public FileBackedTaskManager(File existFile) {
		checkFile(existFile);
	}
	
	public static FileBackedTaskManager loadFromFile(File file) {
		return new FileBackedTaskManager(file);
	}
	
	@Override
	public void createTask(Task task) {
		if (isTaskTimeIntersect(task)) {
			System.out.println("Время выполнения задачи пересекается с другой задачей.");
		}
		super.createTask(task);
		save();
	}
	
	
	@Override
	public void updateEpic(Epic epic) {
		super.updateEpic(epic);
		save();
	}
	
	@Override
	public void removeEpicById(int id) {
		super.removeEpicById(id);
		save();
	}
	
	@Override
	public void removeSubTaskById(int id) {
		super.removeSubTaskById(id);
		save();
	}
	
	@Override
	public void removeTaskById(int id) {
		super.removeTaskById(id);
		save();
	}
	
	
	public void updateSubtask(Subtask subtask) {
		if (isSubtaskTimeIntersect(subtask)) {
			System.out.println("Время выполнения задачи пересекается с другой задачей.");
		}
		super.updateSubtask(subtask);
		save();
	}
	
	public Epic addEpic(Epic epic) {
		super.createEpicTask(epic);
		save();
		return epic;
	}
	
	public Task addTask(Task task) {
		super.createTask(task);
		save();
		return task;
	}
	
	private void checkFile(File existFile) {
		if (!existFile.exists()) {
			System.out.println("Проверка файла на существование");
			Path path = Paths.get(pathToFile);
			try {
				Files.createDirectories(path.getParent());
				if (!Files.exists(path))
					System.out.println("Файл не обнаружен");
			} catch (IOException e) {
				System.out.println("Не удалось создать директорию или файл не обнаружен");
				System.exit(-1);
			}
		}
		load();
	}
	
	private void load() {
		HashMap<Integer, Task> tasks = new HashMap<>();
		HashMap<Integer, Epic> epicTasks = new HashMap<>();
		HashMap<Integer, Subtask> subTasks = new HashMap<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("ID")) continue;  // Пропуск заголовка
				String[] parts = line.split(",");
				
				// Проверка количества элементов
				if (parts.length < 6) {
					System.out.println("Строка содержит недостаточно данных: " + line);
					continue; // Пропуск некорректных строк
				}
				
				int id = Integer.parseInt(parts[0]);
				String type = parts[1];
				String name = parts[2];
				String status = parts[3];
				String description = parts[4];
				
				// Обработка duration и startTime
				long durationMinutes = parts[5].isEmpty() ? 0 : Long.parseLong(parts[5]);
				LocalDateTime startTime = (parts.length > 6 && !parts[6].isEmpty() && !parts[6].equals("null"))
						? LocalDateTime.parse(parts[6])
						: null;
				
				int epicId = parts.length > 7 && !parts[7].isEmpty() ? Integer.parseInt(parts[7]) : 0;
				
				if (type.equals(TypeTask.TASK.name())) {
					Task task = new Task(id, name, TaskStatus.valueOf(status), description);
					task.setDuration(Duration.ofMinutes(durationMinutes));
					task.setStartTime(startTime);
					tasks.put(task.getId(), task);
				}
				
				if (type.equals(TypeTask.SUBTASK.name())) {
					Subtask subtask = new Subtask(id, name, TaskStatus.valueOf(status), description, epicId);
					subtask.setDuration(Duration.ofMinutes(durationMinutes));
					subtask.setStartTime(startTime);
					subTasks.put(subtask.getId(), subtask);
				}
				
				if (type.equals(TypeTask.EPIC.name())) {
					ArrayList<Subtask> subtaskList = new ArrayList<>();
					for (Subtask subtask : subTasks.values()) {
						if (subtask.getEpicId() == id) subtaskList.add(subtask);
					}
					Epic epic = new Epic(id, name, TaskStatus.valueOf(status), description, subtaskList);
					epic.setDuration(Duration.ofMinutes(durationMinutes));
					epic.setStartTime(startTime);
					epicTasks.put(epic.getId(), epic);
				}
			}
			setTasks(tasks);
			setSubTasks(subTasks);
			setEpicTasks(epicTasks);
		} catch (IOException e) {
			System.out.println("Не удалось загрузить задачи из файла: " + e.getMessage());
		} catch (NumberFormatException e) {
			System.out.println("Ошибка при преобразовании числа: " + e.getMessage());
		}
	}
	
	
	public void save() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile))) {
			writer.write("ID,TYPE,NAME,STATUS,DESCRIPTION,DURATION,START_TIME,EPIC\n");
			
			// Сохранение обычных задач
			for (Map.Entry<Integer, Task> task : getTasks().entrySet()) {
				String formatTask = formatTaskForSave(task.getValue());
				writer.write(formatTask + "\n");
			}
			
			// Сохранение эпиков с пересчётом времени
			for (Map.Entry<Integer, Epic> epicEntry : getEpicTasks().entrySet()) {
				Epic epic = epicEntry.getValue();
				// Пересчёт времени эпика
				calculateEpicDurationAndStartTime(epic);
				writer.write(formatTaskForSave(epic) + "\n");
			}
			
			// Сохранение подзадач
			for (Map.Entry<Integer, Subtask> subtaskEntry : getSubTasks().entrySet()) {
				writer.write(formatTaskForSave(subtaskEntry.getValue()) + "\n");
			}
		} catch (IOException e) {
			System.out.println("Failed to save tasks to file: " + e.getMessage());
		}
	}
	
	// Форматирование задачи для записи
	private String formatTaskForSave(Task task) {
		return String.join(",",
				String.valueOf(task.getId()),
				task instanceof Subtask ? TypeTask.SUBTASK.name() : (task instanceof Epic ? TypeTask.EPIC.name() : TypeTask.TASK.name()),
				task.getName(),
				task.getStatus().name(),
				task.getDescription(),
				task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "",
				task.getStartTime() != null ? task.getStartTime().toString() : "",
				task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : ""
		);
	}
	
	// Пересчёт длительности и времени начала эпика
	private void calculateEpicDurationAndStartTime(Epic epic) {
		List<Subtask> subtasks = epic.getSubtasks();
		long totalDuration = subtasks.stream()
				.filter(subtask -> subtask.getDuration() != null)
				.mapToLong(subtask -> subtask.getDuration().toMinutes())
				.sum();
		epic.setDuration(Duration.ofMinutes(totalDuration));
		
		// Установка времени начала эпика по минимальному времени подзадач
		LocalDateTime earliestStartTime = subtasks.stream()
				.filter(subtask -> subtask.getStartTime() != null)
				.map(Subtask::getStartTime)
				.min(LocalDateTime::compareTo)
				.orElse(null);
		epic.setStartTime(earliestStartTime);
	}
	
	
	public List<Task> getSortedTasks() {
		return getTasks().values().stream()
				.sorted(Comparator.comparing(Task::getStartTime)) // сортировка по времени начала
				.collect(Collectors.toList());
	}
	
	public List<Task> getAllSortedTasks() {
		return Stream.concat(
						Stream.concat(getTasks().values().stream(), getEpicTasks().values().stream()),
						getSubTasks().values().stream()
				)
				.sorted(Comparator.comparing(Task::getStartTime)) // сортировка по времени начала
				.collect(Collectors.toList());
	}
	
	
	public boolean isSubtaskTimeIntersect(Subtask newSubtask) {
		return super.isSubtaskTimeIntersect(newSubtask);
	}
	
	
	public boolean isTaskTimeIntersect(Task newTask) {
		return super.isTaskTimeIntersect(newTask);
	}
	
	public boolean isTimeOverlap(Task task1, Task task2) {
		return super.isTimeOverlap(task1, task2);
	}
}
