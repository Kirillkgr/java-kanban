package Server.HttpHandlers;

import Enums.TaskStatus;
import Manager.TaskManager;
import Models.Epic;
import Models.Subtask;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends AbstractHandler {
	
	public EpicsHandler(TaskManager manager) {
		super(manager);
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod();
		String path = exchange.getRequestURI().getPath();
		
		try {
			if ("GET".equals(method) && path.equals("/epics")) {
				handleGetEpics(exchange);
			} else if ("GET".equals(method) && path.startsWith("/epics/") && path.endsWith("/subtasks")) {
				handleGetEpicSubtasks(exchange);
			} else if ("GET".equals(method) && path.startsWith("/epics/")) {
				handleGetEpicById(exchange);
			} else if ("POST".equals(method) && path.equals("/epics")) {
				handleCreateOrUpdateEpic(exchange);
			} else if ("DELETE".equals(method) && path.startsWith("/epics/")) {
				handleDeleteEpic(exchange);
			} else {
				exchange.sendResponseHeaders(404, -1); // Если путь не найден
			}
		} catch (Exception e) {
			e.printStackTrace();
			exchange.sendResponseHeaders(500, -1); // Общая ошибка сервера
		}
	}
	
	// Метод для получения всех эпиков
	private void handleGetEpics(HttpExchange exchange) throws IOException {
		List<Epic> epics = getTaskManager().getAllEpics();
		epics.stream().filter(e -> e.getStartTime() == null).forEach(e -> {
			e.setStartTime(LocalDateTime.now());
		});
		String response = toJson(epics); // Преобразование в JSON
		sendResponse(exchange, 200, response);
	}
	
	// Метод для получения эпика по ID_
	private void handleGetEpicById(HttpExchange exchange) throws IOException {
		String[] pathSegments = exchange.getRequestURI().getPath().split("/");
		int id = Integer.parseInt(pathSegments[pathSegments.length - 1]);
		
		Optional<Epic> epicOptional = Optional.ofNullable((Epic) getTaskManager().getEpicTaskById(id));
		if (epicOptional.isEmpty()) {
			exchange.sendResponseHeaders(404, -1); // Если эпика нет
		} else {
			String response = toJson(epicOptional.get());
			sendResponse(exchange, 200, response);
		}
	}
	
	// Метод для получения подзадач конкретного эпика по его ID
	private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
		String[] pathSegments = exchange.getRequestURI().getPath().split("/");
		int id = Integer.parseInt(pathSegments[pathSegments.length - 2]);
		
		Epic epic = (Epic) getTaskManager().getEpicTaskById(id);
		if (epic == null) {
			exchange.sendResponseHeaders(404, -1); // Если эпика нет
		} else {
			List<Subtask> subtasks = getTaskManager().getSubtasksOfEpic(id);
			String response = toJson(subtasks);
			sendResponse(exchange, 200, response);
		}
	}
	
	// Метод для создания или обновления эпика
	private void handleCreateOrUpdateEpic(HttpExchange exchange) throws IOException {
		String requestBody = new String(exchange.getRequestBody().readAllBytes());
		if (requestBody == null || requestBody.isEmpty()) {
			exchange.sendResponseHeaders(400, -1); // Ошибка в запросе
			return;
		}
		
		Epic epic = fromJson(requestBody, Epic.class);
		int newEpicId;
		if (epic.getId() == null) {
			newEpicId = getTaskManager().createEpicTask(epic).getId();
			sendResponse(exchange, newEpicId == -1 ? 409 : 201, newEpicId == -1 ? "" : String.valueOf(newEpicId)); // Эпик создан
		} else {
			getTaskManager().updateEpic(epic);
			exchange.sendResponseHeaders(200, 0); // Эпик обновлен
		}
		exchange.getResponseBody().close();
	}
	
	//	 Метод для удаления эпика по ID
	private void handleDeleteEpic(HttpExchange exchange) throws IOException {
		String[] pathSegments = exchange.getRequestURI().getPath().split("/");
		int id = Integer.parseInt(pathSegments[pathSegments.length - 1]);
		
		boolean isDeleted = getTaskManager().removeEpicById(id);
		if (isDeleted) {
			exchange.sendResponseHeaders(200, 0); // Успешное удаление эпика
		} else {
			exchange.sendResponseHeaders(404, -1); // Эпик не найден
		}
		exchange.getResponseBody().close();
	}
}
