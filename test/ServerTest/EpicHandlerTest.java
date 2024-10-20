package ServerTest;

import Enums.TaskStatus;
import Manager.Impl.FileBackedTaskManager;
import Models.Epic;
import Models.Subtask;
import Server.HttpTaskServer;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicHandlerTest extends AbstractTestManager {
	
	HttpTaskServer taskServer = new HttpTaskServer();
	
	EpicHandlerTest() {
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
	public void testCreateAndGetEpic() throws IOException, InterruptedException {
		// ---------------------- Create Epic ----------------------
		Epic epic = new Epic("Epic 1", "Epic description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
		String epicJson = toJson(epic);
		
		HttpClient client = HttpClient.newHttpClient();
		URI urlCreate = URI.create("http://localhost:8080/epics");
		HttpRequest requestCreate = HttpRequest.newBuilder().uri(urlCreate).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
		HttpResponse<String> responseCreate = client.send(requestCreate, HttpResponse.BodyHandlers.ofString());
		
		assertEquals(201, responseCreate.statusCode(), "Epic creation failed");
		
		// ---------------------- Get All Epics ----------------------
		URI urlGetAll = URI.create("http://localhost:8080/epics");
		HttpRequest requestGetAll = HttpRequest.newBuilder().uri(urlGetAll).GET().build();
		HttpResponse<String> responseGetAll = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());
		
		assertEquals(200, responseGetAll.statusCode(), "Epic retrieval failed");
		
		Type listType = new TypeToken<List<Epic>>() {}.getType();
		List<Epic> epics = fromJsonArray(responseGetAll.body(), listType);
		
		assertNotNull(epics, "Epics list is null");
		assertEquals(1, epics.size(), "Incorrect number of epics returned");
		assertEquals("Epic 1", epics.getFirst().getName(), "Epic name mismatch");
	}
	
	@Test
	public void testUpdateEpic() throws IOException, InterruptedException {
		// Создание эпика для обновления
		Epic epic = new Epic("Epic to update", "Initial description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
		String epicJson = toJson(epic);
		
		HttpClient client = HttpClient.newHttpClient();
		URI urlCreate = URI.create("http://localhost:8080/epics");
		HttpRequest requestCreate = HttpRequest.newBuilder().uri(urlCreate).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
		HttpResponse<String> responseCreate = client.send(requestCreate, HttpResponse.BodyHandlers.ofString());
		assertEquals(201, responseCreate.statusCode(), "Epic creation failed");
		
		// Обновление эпика
		Epic updatedEpic = new Epic(1, "Updated Epic", "Updated description", TaskStatus.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now());
		String updatedEpicJson = toJson(updatedEpic);
		
		URI urlUpdate = URI.create("http://localhost:8080/epics");
		HttpRequest requestUpdate = HttpRequest.newBuilder().uri(urlUpdate).POST(HttpRequest.BodyPublishers.ofString(updatedEpicJson)).build();
		HttpResponse<String> responseUpdate = client.send(requestUpdate, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, responseUpdate.statusCode(), "Epic update failed");
		
		// Проверка обновленного эпика
		URI urlGetById = URI.create("http://localhost:8080/epics/1");
		HttpRequest requestGetById = HttpRequest.newBuilder().uri(urlGetById).GET().build();
		HttpResponse<String> responseGetById = client.send(requestGetById, HttpResponse.BodyHandlers.ofString());
		assertEquals(200, responseGetById.statusCode(), "Failed to retrieve updated epic");
		
		Epic retrievedEpic = fromJson(responseGetById.body(), Epic.class);
		assertEquals("Updated Epic", retrievedEpic.getName(), "Epic name was not updated");
		assertEquals(TaskStatus.IN_PROGRESS, retrievedEpic.getStatus(), "Epic status was not updated");
	}
	
	@Test
	public void testDeleteEpic() throws IOException, InterruptedException {
		// Создание эпика для удаления
		Epic epic = new Epic("Epic to delete", "Epic description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
		String epicJson = toJson(epic);
		
		HttpClient client = HttpClient.newHttpClient();
		URI urlCreate = URI.create("http://localhost:8080/epics");
		HttpRequest requestCreate = HttpRequest.newBuilder().uri(urlCreate).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
		HttpResponse<String> responseCreate = client.send(requestCreate, HttpResponse.BodyHandlers.ofString());
		assertEquals(201, responseCreate.statusCode(), "Epic creation failed");
		
		// Удаление эпика
		URI urlDelete = URI.create("http://localhost:8080/epics/1");
		HttpRequest requestDelete = HttpRequest.newBuilder().uri(urlDelete).DELETE().build();
		HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
		assertEquals(404, responseDelete.statusCode(), "Epic deletion failed");
		
		// Проверка, что эпик удален
		URI urlGetById = URI.create("http://localhost:8080/epics/1");
		HttpRequest requestGetById = HttpRequest.newBuilder().uri(urlGetById).GET().build();
		HttpResponse<String> responseGetById = client.send(requestGetById, HttpResponse.BodyHandlers.ofString());
		assertEquals(404, responseGetById.statusCode(), "Epic was not deleted");
	}
}
