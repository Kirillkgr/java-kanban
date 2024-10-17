package Server.HttpHandlers;

import Manager.TaskManager;
import Models.Task;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends AbstractHandler {
	
	public TaskHandler(TaskManager manager) {
		super(manager);
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod();
		String path = exchange.getRequestURI().getPath();
		
		try {
			if ("GET".equals(method) && path.equals("/tasks")) {
				handleGetTasks(exchange);
			} else if ("GET".equals(method) && path.startsWith("/tasks/")) {
				handleGetTaskById(exchange);
			} else if ("POST".equals(method) && path.equals("/tasks")) {
				handleCreateOrUpdateTask(exchange);
			} else if ("DELETE".equals(method) && path.startsWith("/tasks/")) {
				handleDeleteTask(exchange);
			} else {
				exchange.sendResponseHeaders(404, -1); // Если путь не найден
			}
		} catch (Exception e) {
			e.printStackTrace();
			exchange.sendResponseHeaders(500, -1); // Общая ошибка сервера
		}
	}
	
	// Метод для получения всех задач
	private void handleGetTasks(HttpExchange exchange) throws IOException {
		List<Task> tasks = getTaskManager().getAllTasks();
		
		String response = toJson(tasks); // Преобразование в JSON
		sendResponse(exchange, 200, response);
	}
	
	
	// Метод для получения задачи по ID
	private void handleGetTaskById(HttpExchange exchange) throws IOException {
		String[] pathSegments = exchange.getRequestURI().getPath().split("/");
		int id = Integer.parseInt(pathSegments[pathSegments.length - 1]);
		
		Optional<Task> taskOptional = getTaskManager().getTaskById(id);
		ParserTask(exchange, taskOptional.isPresent(), taskOptional.get());
	}
	
	static void ParserTask(HttpExchange exchange, boolean present, Task task) throws IOException {
		if (present) {
			String response = toJson(task);
			sendResponse(exchange, 200, response);
		} else {
			exchange.sendResponseHeaders(404, -1); // Если задачи с таким ID нет
		}
	}
	
	// Метод для создания или обновления задачи
	private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
		String requestBody = new String(exchange.getRequestBody().readAllBytes());
		if (requestBody==null || requestBody.isEmpty()) {
			exchange.sendResponseHeaders(400, -1); // Ошибка в запросе
			return;
		}
		
		Task task = fromJson(requestBody, Task.class);
		
		if (task.getId() == null) {
			int newTaskId = getTaskManager().createTask(task);
			exchange.sendResponseHeaders(201, 0); // Успешное создание задачи
		} else {
			int hashBefore = task.hashCode();
			int hashAfter = getTaskManager().updateTask(task).hashCode();
			if (hashBefore == hashAfter) {
				exchange.sendResponseHeaders(200, 0); // Успешное обновление задачи
			} else {
				exchange.sendResponseHeaders(409, -1); // Конфликт — пересечение с существующими задачами
			}
		}
		exchange.getResponseBody().close();
	}
	
	// Метод для удаления задачи по ID
	private void handleDeleteTask(HttpExchange exchange) throws IOException {
		String[] pathSegments = exchange.getRequestURI().getPath().split("/");
		int id = Integer.parseInt(pathSegments[pathSegments.length - 1]);
		
		boolean isDeleted = getTaskManager().removeTaskById(id);
		if (isDeleted) {
			exchange.sendResponseHeaders(200, 0); // Успешное удаление задачи
		} else {
			exchange.sendResponseHeaders(404, -1); // Если задачи с таким ID нет
		}
		exchange.getResponseBody().close();
	}
}
