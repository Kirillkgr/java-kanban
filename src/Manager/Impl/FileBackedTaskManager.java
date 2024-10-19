package Manager.Impl;

import Enums.TaskStatus;
import Enums.TypeTask;
import Exeptions.FileException;
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
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

public class FileBackedTaskManager extends InMemoryTaskManager {
	private final String pathToFile = System.getProperty("user.home") + File.separator + "tasks.csv";
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	public FileBackedTaskManager(File existFile) {
		checkFile(existFile);
	}
	
	public static FileBackedTaskManager loadFromFile(File existFile) {
		return new FileBackedTaskManager(existFile);
	}
	
	public TreeSet<Task> getPrioritizedTasks() {
		return super.getPrioritizedTasks();
	}
	
	@Override
	public int createTask(Task task) {
		int taskId = super.createTask(task);
		if (taskId == -1)
			return -1;
		super.getPrioritizedTasks().add(task);
		save();
		return taskId;
	}
	
	
	@Override
	public void updateEpic(Epic epic) {
		super.updateEpic(epic);
		save();
	}
	
	@Override
	public boolean removeEpicById(int id) {
		if (super.removeEpicById(id)) {
			save();
		}
		return false;
	}
	
	@Override
	public boolean removeSubTaskById(int id) {
		Subtask subtask = null;
		if (getSubTaskById(id).isPresent())
			subtask = getSubTaskById(id).get();
		if (subtask != null) {
			super.getPrioritizedTasks().remove(subtask);
		}
		boolean isRemoved = super.removeSubTaskById(id);
		save();
		return isRemoved;
	}
	
	@Override
	public boolean removeTaskById(Integer id) {
		boolean isRemoved = super.removeTaskById(id);
		if (isRemoved) {
			save();
		}
		return isRemoved;
		
	}
	
	@Override
	public boolean updateSubtask(Subtask subtask) {
		boolean isUpdated = super.updateSubtask(subtask);
		super.getPrioritizedTasks().add(subtask);
		save();
		return isUpdated;
	}
	
	public Epic addEpic(Epic epic) {
		super.createEpicTask(epic);
		super.getPrioritizedTasks().add(epic);
		save();
		return epic;
	}
	
	public Task addTask(Task task) {
		super.createTask(task);
		super.getPrioritizedTasks().add(task);
		save();
		return task;
	}
	
	private void checkFile(File existFile) {
		if (!existFile.exists()) {
			System.out.println("Проверка файла на существование");
			Path path = Paths.get(pathToFile);
			try {
				Files.createDirectories(path.getParent());
				if (!Files.exists(path)) {
					System.out.println("Файл не обнаружен");
				} else
					System.out.println("Файла существует");
			} catch (FileException | IOException e) {
				throw new FileException("Не удалось создать директорию или файл не обнаружен");
			}
		}
		load();
	}
	
	// Обновляем метод загрузки с учетом новых полей
	private void load() {
		try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("ID")) continue;
				String[] parts = line.split(",");
				int id = Integer.parseInt(parts[0]);
				String type = parts[1];
				String name = parts[2];
				String status = parts[3];
				String description = parts[4];
				LocalDateTime startTime = null;
				Duration duration = null;
				if (parts.length > 5) startTime = !parts[5].isEmpty() ? LocalDateTime.parse(parts[5], formatter) : null;
				if (parts.length > 6)
					duration = !parts[6].isEmpty() ? Duration.ofMinutes(Long.parseLong(parts[6])) : null;
				
				if (type.equals(TypeTask.TASK.name())) {
					Task task = new Task(id, name, description, TaskStatus.valueOf(status), duration, startTime);
					getTasks().put(task.getId(), task);
				}
				
				if (type.equals(TypeTask.SUBTASK.name())) {
					int epicId = Integer.parseInt(parts[7]);
					Subtask subtask = new Subtask(id, name, description, TaskStatus.valueOf(status), duration, startTime, epicId);
					getSubTasks().put(subtask.getId(), subtask);
				}
				
				if (type.equals(TypeTask.EPIC.name())) {
					Epic epic = new Epic(id, name, TaskStatus.valueOf(status), description, duration, startTime);
					getEpicTasks().put(epic.getId(), epic);
				}
			}
		} catch (IOException e) {
			System.out.println("Не удалось загрузить задачи из файла: " + e.getMessage());
		}
	}
	
	
	public void save() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile))) {
			writer.write("ID,TYPE,NAME,STATUS,DESCRIPTION,START_TIME,DURATION,EPIC\n");
			for (Task task : getTasks().values()) {
				writer.write(taskToString(task) + "\n");
			}
			for (Epic epic : getEpicTasks().values()) {
				writer.write(taskToString(epic) + "\n");
			}
			for (Subtask subtask : getSubTasks().values()) {
				writer.write(taskToString(subtask) + "\n");
			}
		} catch (IOException e) {
			System.out.println("Failed to save tasks to file: " + e.getMessage());
		}
	}
	
	public String taskToString(Task task) {
		String startTime = (task.getStartTime() != null) ? task.getStartTime().format(formatter) : "";
		String duration = (task.getDuration() != null) ? String.valueOf(task.getDuration().toMinutes()) : "";
		int epicId = 0;
		if (task instanceof Subtask) {
			epicId = ((Subtask) task).getEpicId();
		}
		return String.format("%d,%s,%s,%s,%s,%s,%s,%s", task.getId(), task instanceof Subtask ? TypeTask.SUBTASK : (task instanceof Epic ? TypeTask.EPIC : TypeTask.TASK), task.getName(), task.getStatus(), task.getDescription(), startTime, duration, epicId == 0 ? "" : epicId);
	}
	
	
}
