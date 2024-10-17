package Server.HttpHandlers;

import Manager.TaskManager;
import Models.Subtask;
import com.sun.net.httpserver.HttpHandler;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends AbstractHandler  {
	public SubtaskHandler(TaskManager manager) {
		super(manager);
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod();
		String path = exchange.getRequestURI().getPath();
		
		try {
			if ("GET".equals(method) && path.equals("/subtasks")) {
				handleGetSubtasks(exchange);
			} else if ("GET".equals(method) && path.startsWith("/subtasks/")) {
				handleGetSubtaskById(exchange);
			} else if ("POST".equals(method) && path.equals("/subtasks")) {
				handleCreateOrUpdateSubtask(exchange);
			} else if ("DELETE".equals(method) && path.startsWith("/subtasks/")) {
				handleDeleteSubtask(exchange);
			} else {
				exchange.sendResponseHeaders(404, -1); // Если путь не найден
			}
		} catch (Exception e) {
			e.printStackTrace();
			exchange.sendResponseHeaders(500, -1); // Общая ошибка сервера
		}
	}
	
	// Метод для получения всех подзадач
	private void handleGetSubtasks(HttpExchange exchange) throws IOException {
		List<Subtask> subtasks = getTaskManager().getAllSubtasks();
		String response = toJson(subtasks); // Преобразование в JSON
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
	
	// Метод для получения подзадачи по ID
	private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
		String[] pathSegments = exchange.getRequestURI().getPath().split("/");
		int id = Integer.parseInt(pathSegments[pathSegments.length - 1]);
		
		Optional<Subtask> subtaskOptional = getTaskManager().getSubTaskById(id);
		TaskHandler.ParserTask(exchange, subtaskOptional.isPresent(), subtaskOptional.get());
	}
	
	// Метод для создания или обновления подзадачи
	private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
		String requestBody = new String(exchange.getRequestBody().readAllBytes());
		Subtask subtask = fromJson(requestBody, Subtask.class);
		
		if (subtask.getId() == null) {
			boolean newSubtaskId = getTaskManager().createSubTask(subtask);
			if (newSubtaskId) {
				exchange.sendResponseHeaders(201, 0); // Успешное создание подзадачи
			} else {
				exchange.sendResponseHeaders(409, -1); // Конфликт — пересечение с существующими подзадачами
			}
		} else {
			boolean isUpdated = getTaskManager().updateSubtask(subtask);
			if (isUpdated) {
				exchange.sendResponseHeaders(200, 0); // Успешное обновление подзадачи
			} else {
				exchange.sendResponseHeaders(409, -1); // Конфликт — пересечение с существующими подзадачами
			}
		}
		exchange.getResponseBody().close();
	}
	
	// Метод для удаления подзадачи по ID
	private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
		String[] pathSegments = exchange.getRequestURI().getPath().split("/");
		int id = Integer.parseInt(pathSegments[pathSegments.length - 1]);
		
		boolean isDeleted = getTaskManager().removeSubTaskById(id);
		if (isDeleted) {
			exchange.sendResponseHeaders(200, 0); // Успешное удаление подзадачи
		} else {
			exchange.sendResponseHeaders(404, -1); // Если подзадачи с таким ID нет
		}
		exchange.getResponseBody().close();
	}
}
