import Enums.TaskStatus;
import Manager.Impl.FileBackedTaskManager;
import Manager.TaskManager;
import Models.Epic;
import Models.Subtask;
import Models.Task;

import Server.HttpTaskServer;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) {
		System.out.println("Поехали!");
		HttpTaskServer httpTaskServer = new HttpTaskServer();
		httpTaskServer.start();
		
		TaskManager tracker = new FileBackedTaskManager(new File(System.getProperty("user.home") + File.separator + "tasks.csv"));
		
		Task task1 = new Task("Task 1", "Description task 1");
		Task task2 = new Task("Task 2", "Description task 2");
		
		
		task1.setStartTime(LocalDateTime.now());
		task1.setDuration(Duration.ofMinutes(60));
		
		task2.setStartTime(LocalDateTime.now().plusHours(2));
		task2.setDuration(Duration.ofMinutes(90));
		
		tracker.createTask(task1);
		tracker.createTask(task2);
		
		System.out.println("\nСоздалась первая и  вторая простая задача\n" + tracker.getAllTasks());
		task1.setDescription("new Description");
		
		tracker.updateTask(task2);
		System.out.println("\nОбновилась вторая простая задача\n" + tracker.getAllTasks());
		
		tracker.removeTaskById(task1.getId());
		System.out.println("\nУдалилась первая простая задача\n" + tracker.getAllTasks());
		
		
		Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
		epic1 = tracker.createEpicTask(epic1);
		Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", epic1.getId());
		Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", epic1.getId());
		Subtask subtask3 = new Subtask("Subtask 3", "Description of Subtask 3", epic1.getId());
		
		subtask1.setStartTime(LocalDateTime.now().plusHours(5));
		subtask1.setDuration(Duration.ofMinutes(10));
		
		subtask2.setStartTime(LocalDateTime.now().plusHours(6));
		subtask2.setDuration(Duration.ofMinutes(10));
		
		subtask3.setStartTime(LocalDateTime.now().plusHours(7));
		subtask3.setDuration(Duration.ofMinutes(10));
		
		subtask1.setEpicId(epic1.getId());
		subtask2.setEpicId(epic1.getId());
		subtask3.setEpicId(epic1.getId());
		
		tracker.createSubTask(subtask1);
		tracker.createSubTask(subtask2);
		
		System.out.println("\nСоздалась тестовая задача с двумя под задачами\n" + tracker.getEpicTaskById(epic1.getId()));
		
		
		tracker.createSubTask(subtask3);
		System.out.println("\nДобавилась третия под задача\n" + tracker.getEpicTaskById(epic1.getId()));
		
		subtask1.setStatus(TaskStatus.DONE);
		subtask2.setStatus(TaskStatus.IN_PROGRESS);
		
		tracker.updateSubtask(subtask1);
		System.out.println("\nСменили тип первой под задачи на Done и второй на IN_Progress\n" + tracker.getEpicTaskById(epic1.getId()));
		
		tracker.removeSubTaskById(subtask1.getId());
		tracker.removeSubTaskById(subtask3.getId());
		System.out.println("\nУдалили первый и третию подзадачу\n" + tracker.getEpicTaskById(epic1.getId()));
		
		subtask2.setStatus(TaskStatus.DONE);
		tracker.updateSubtask(subtask2);
		System.out.println("\nСменили тип второй на DONE\n" + tracker.getEpicTaskById(epic1.getId()));
		
		Subtask subtask4 = new Subtask("Subtask 3", "Description of Subtask 3", epic1.getId());
		subtask4.setEpicId(epic1.getId());
		tracker.createSubTask(subtask4);
		System.out.println("\nДобавилась четвертая под задача\n" + tracker.getEpicTaskById(epic1.getId()));
		
		subtask4.setDescription("This new description");
		tracker.updateSubtask(subtask4);
		System.out.println("\nИзменили описаание 4 под задачи\n" + tracker.getEpicTaskById(epic1.getId()));
		
		epic1.setDescription("This new description");
		epic1.updateStatus();
		System.out.println("\nИзменили описание Epic\n" + tracker.getAllEpics());
		
		System.out.println("\nВывод всех задач типа NEW\n" + tracker.getAllEpics().stream().filter(epic -> epic.getStatus() == TaskStatus.NEW).toList());
		
		System.out.println("\nВывод всех задач типа IN_PROGRESS\n" + tracker.getAllEpics().stream().filter(epic -> epic.getStatus() == TaskStatus.IN_PROGRESS).toList());
		
		System.out.println("\nВывод всех задач типа DONE\n" + tracker.getAllEpics().stream().filter(epic -> epic.getStatus() == TaskStatus.DONE).toList());
		
		System.out.println("\nПолучение всех задач под задачь используя EpicId \n" + tracker.getSubtasksOfEpic(epic1.getId()));
		
		System.out.println("\nВывод всех задач\n" + (tracker.getAllTasks().isEmpty() ? "Список пуст" : tracker.getAllTasks()));
		
		System.out.println("\nВывод всех Epic задачь\n" + tracker.getAllEpics());
		
		tracker.getAllSubtasks();
		System.out.println("\nВывод всех Subtasks задачь\n" + (tracker.getAllSubtasks().isEmpty() ? "Список под задачь пуст" : tracker.getAllSubtasks()));
		
		Epic epic2 = new Epic("Epic 2", "Description of Epic 2");
		epic2 = tracker.createEpicTask(epic2);
		
		// 3. Запрос задач в разном порядке
		tracker.getTaskById(task2.getId());
		tracker.getEpicTaskById(epic2.getId());
		tracker.getSubTaskById(subtask4.getId());
		tracker.getTaskById(task2.getId());
		tracker.getSubTaskById(subtask2.getId());
		
		System.out.println("\nИстория после различных запросов:");
		System.out.println(tracker.getHistory());
		
		// 4. Удаление задачи, которая есть в истории, и проверка истории
		tracker.removeTaskById(task2.getId());
		System.out.println("\nИстория после удаления Task 2:");
		System.out.println(tracker.getHistory());
		
		// 5. Удаление эпика с тремя подзадачами и проверка истории
		tracker.removeEpicById(epic2.getId());
		System.out.println("\nИстория после удаления Epic 2 и его подзадач:");
		System.out.println(tracker.getHistory());
		
		tracker.removeAllTasks();
		System.out.println("\nУдаление всех Task задачь \n" + tracker.getAllTasks());
		tracker.removeAllSubTasks();
		System.out.println("\nУдаление всех Subtask задачь \n" + tracker.getAllSubtasks());
		
		tracker.removeAllEpicTasks();
		System.out.println("\nУдаление всех Epic задачь \n" + tracker.getAllEpics());
		
		// Проверка работы FileBackedTaskManager
		File file = new File(System.getProperty("user.home") + File.separator + "tasks.csv");
		
		FileBackedTaskManager manager1 = new FileBackedTaskManager(file);
		Task taskForTestFile1 = new Task(1, "taskForTestFile 1", TaskStatus.NEW, "Description taskForTestFile1");
		Epic epicForTestFile1 = new Epic(2, "epicForTestFile 1", TaskStatus.NEW, "Description epicForTestFile1", new ArrayList<>());
		
		taskForTestFile1.setDuration(Duration.ofMinutes(4));
		taskForTestFile1.setStartTime(LocalDateTime.now());
		
		Subtask subTaskForTestFile = new Subtask(1, "SubTaskForTestFile 1", TaskStatus.NEW, "Description taskForTestFile1", epicForTestFile1.getId());
		subTaskForTestFile.setDuration(Duration.ofMinutes(4));
		subTaskForTestFile.setStartTime(LocalDateTime.now());
		
		epicForTestFile1.addSubtask(subTaskForTestFile);
		
		manager1.addTask(taskForTestFile1);
		manager1.addEpic(epicForTestFile1);
		
		
		FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);
		
		
		System.out.println(manager1.getTasks().equals(manager2.getTasks()));  // true
		System.out.println(manager1.getEpicTasks().equals(manager2.getEpicTasks()));  // true
		
		httpTaskServer.stop(); // Завершение работы сервера
	}
}
