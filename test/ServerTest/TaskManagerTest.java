package ServerTest;

import Enums.TaskStatus;
import Manager.Impl.FileBackedTaskManager;
import Models.Task;
import Server.HttpTaskServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskManagerTest extends AbstractTestManager {
	
	HttpTaskServer taskServer = new HttpTaskServer();
	
	TaskManagerTest() {
		super(new FileBackedTaskManager(new File("tasks_1.csv")));
	}
	
	@BeforeEach
	public void setUp() {
		
		getTaskManager().removeAllTasks();
		getTaskManager().removeAllEpicTasks();
		getTaskManager().removeAllSubTasks();
		taskServer.start();
	}
	
	@AfterEach
	public void shutDown() {
		taskServer.stop();
	}
	
	@Test
	public void testAddTask() throws IOException, InterruptedException {
		// создаём задачу
		Task task = new Task("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
		// конвертируем её в JSON
		String taskJson = toJson(task);
		// ---------------------- Create task ----------------------
		// создаём HTTP-клиент и запрос
		HttpClient clientCreate = HttpClient.newHttpClient();
		URI urlCreateTask = URI.create("http://localhost:8080/tasks");
		HttpRequest requestAfterCreate = HttpRequest.newBuilder().uri(urlCreateTask).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> responseAfterCreate = clientCreate.send(requestAfterCreate, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(201, responseAfterCreate.statusCode());
		
		// ---------------------- Get all task ----------------------
		// создаём HTTP-клиент и запрос
		HttpClient clientGetAll = HttpClient.newHttpClient();
		URI url = URI.create("http://localhost:8080/tasks");
		HttpRequest requestGetAll = HttpRequest.newBuilder().uri(url).GET().build();
		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> response = clientGetAll.send(requestGetAll, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, response.statusCode());
		
		
		Type listType = new TypeToken<List<Task>>() {
		}.getType();
		List<Task> tasksFromManager = fromJsonArray(response.body(), listType);
		
		assertNotNull(tasksFromManager, "Задачи не возвращаются");
		assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
		assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
		// ---------------------- Get updated task ----------------------
		
		Task taskUpdate = new Task(1, "Test 1", "Testing task update", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
		// конвертируем её в JSON
		String taskJsonUpdate = toJson(taskUpdate);
		// создаём HTTP-клиент и запрос
		HttpClient clientUpdate = HttpClient.newHttpClient();
		URI urlUpdateTask = URI.create("http://localhost:8080/tasks");
		HttpRequest requestAfterUpdate = HttpRequest.newBuilder().uri(urlUpdateTask).POST(HttpRequest.BodyPublishers.ofString(taskJsonUpdate)).build();
		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> responseAfterUpdate = clientUpdate.send(requestAfterUpdate, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, responseAfterUpdate.statusCode());
		
		
		// ---------------------- Get all task ----------------------
		// создаём HTTP-клиент и запрос
		HttpClient clientGetById = HttpClient.newHttpClient();
		URI urlGetById = URI.create("http://localhost:8080/tasks/1");
		HttpRequest requestGetById = HttpRequest.newBuilder().uri(urlGetById).GET().build();
		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> responseGetById = clientGetById.send(requestGetById, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, responseGetById.statusCode());
		Task taskFromManager = fromJson(responseGetById.body(), Task.class);
		assertEquals(1, taskFromManager.getId(), "Id not equal");
		
		// ---------------------- Delete task ----------------------
		// создаём HTTP-клиент и запрос
		HttpClient clientDelete = HttpClient.newHttpClient();
		URI urlDeleteTask = URI.create("http://localhost:8080/tasks/1");
		HttpRequest requestAfterDelete = HttpRequest.newBuilder().uri(urlDeleteTask).DELETE().build();
		// вызываем рест, отвечающий за создание задач
		HttpResponse<String> responseAfterDelete = clientDelete.send(requestAfterDelete, HttpResponse.BodyHandlers.ofString());
		// проверяем код ответа
		assertEquals(200, responseAfterDelete.statusCode());
	}
}