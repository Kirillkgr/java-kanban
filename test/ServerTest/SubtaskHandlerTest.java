package ServerTest;

import Enums.TaskStatus;
import Manager.Impl.FileBackedTaskManager;
import Models.Epic;
import Models.Subtask;
import Models.Task;
import Server.HttpTaskServer;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubtaskHandlerTest extends AbstractTestManager {
	private HttpTaskServer taskServer;
	
	public SubtaskHandlerTest() {
		super(new FileBackedTaskManager(new File("subtasks_test.csv")));
	}
	
	@BeforeEach
	public void setUp() {
		getTaskManager().removeAllTasks();
		getTaskManager().removeAllEpicTasks();
		getTaskManager().removeAllSubTasks();
		taskServer = new HttpTaskServer();
		taskServer.start();
	}
	
	@AfterEach
	public void tearDown() {
		getTaskManager().removeAllEpicTasks();
		getTaskManager().removeAllSubTasks();
		getTaskManager().removeAllTasks();
		taskServer.stop();
	}
	
	@Test
	public void testCreateSubtask() throws IOException, InterruptedException {
		Epic epic = new Epic("Epic to test SubTask", "Initial description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
		String epicJson = toJson(epic);
		try (HttpClient client = HttpClient.newHttpClient()) {
			
			
			URI urlCreateEpic = URI.create("http://localhost:8080/epics");
			HttpRequest requestCreate = HttpRequest.newBuilder().uri(urlCreateEpic).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
			HttpResponse<String> responseCreateEpic = client.send(requestCreate, HttpResponse.BodyHandlers.ofString());
			assertEquals(201, responseCreateEpic.statusCode(), "Epic creation failed");
			
			URI urlGetEpicById = URI.create("http://localhost:8080/epics/1");
			HttpRequest requestGetEpicById = HttpRequest.newBuilder().uri(urlGetEpicById).GET().build();
			HttpResponse<String> responseGetById = client.send(requestGetEpicById, HttpResponse.BodyHandlers.ofString());
			
			Epic retrievedEpic = fromJson(responseGetById.body(), Epic.class);
			assertEquals(200, responseGetById.statusCode(), "Failed to retrieve updated epic");
			
			// Создаем подзадачу
			Subtask subtask = new Subtask("Subtask 1", "Description 1", retrievedEpic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
			String subtaskJson = toJson(subtask);
			
			// Создание подзадачи (POST /subtasks)
			
			URI url = URI.create("http://localhost:8080/subtasks");
			HttpRequest request = HttpRequest.newBuilder()
					.uri(url)
					.POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			// Проверяем, что подзадача успешно создана
			assertEquals(201, response.statusCode());
			
			
			URI urlGetById = URI.create("http://localhost:8080/subtasks");
			HttpRequest requestGetById = HttpRequest.newBuilder().uri(urlGetById).GET().build();
			HttpResponse<String> responseGetByID = client.send(requestGetById, HttpResponse.BodyHandlers.ofString());
			Type listType = new TypeToken<List<Task>>() {
			}.getType();
			
			// Проверяем, что запрос успешен и подзадача найдена
			assertEquals(201, response.statusCode());
			List<Subtask> fetchedSubtask = fromJsonArray(responseGetByID.body(), listType);
			assertEquals(1, fetchedSubtask.size());
		}
	}
	
	@Test
	public void testGetAllSubtasks() throws IOException, InterruptedException {
		Epic epic = new Epic("Epic 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
		String epicJson = toJson(epic);
		
		try (HttpClient client = HttpClient.newHttpClient()) {
			
			URI urlCreateEpic = URI.create("http://localhost:8080/epics");
			HttpRequest requestCreateEpic = HttpRequest.newBuilder().uri(urlCreateEpic).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
			assertEquals(201, client.send(requestCreateEpic, HttpResponse.BodyHandlers.ofString()).statusCode(), "Epic creation failed");
			
			URI urlGetById = URI.create("http://localhost:8080/epics/1");
			HttpRequest requestGetById = HttpRequest.newBuilder().uri(urlGetById).GET().build();
			HttpResponse<String> responseGetById = client.send(requestGetById, HttpResponse.BodyHandlers.ofString());
			assertEquals(200, responseGetById.statusCode(), "Failed to retrieve updated epic");
			
			Epic retrievedEpic = fromJson(responseGetById.body(), Epic.class);
			
			// Добавляем несколько подзадач
			Subtask subtask1 = new Subtask("Subtask 1", "Description 1", retrievedEpic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
			Subtask subtask2 = new Subtask("Subtask 2", "Description 1", retrievedEpic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(30));
			Subtask subtask3 = new Subtask("Subtask 3", "Description 1", retrievedEpic.getId(), Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
			List<Subtask> subtasks = List.of(subtask1, subtask2, subtask3);
			
			URI url = URI.create("http://localhost:8080/subtasks");
			for (Subtask subtask : subtasks) {
				String subtaskJson = toJson(subtask);
				HttpRequest requestCreate = HttpRequest.newBuilder()
						.uri(url)
						.POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
						.build();
				assertEquals(201, client.send(requestCreate, HttpResponse.BodyHandlers.ofString()).statusCode());
			}
			
			
			HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			// Проверяем успешность запроса и количество подзадач
			assertEquals(200, response.statusCode());
			List<Subtask> subtasksResponse = fromJsonArray(response.body(), List.class);
			assertNotNull(subtasksResponse);
			assertEquals(3, subtasksResponse.size());
		}
	}
}
