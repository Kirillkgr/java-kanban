package Server.HttpHandlers;

import Manager.TaskManager;
import Models.Task;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.TreeSet;

public class PrioritizedHandler extends AbstractHandler {
	
	public PrioritizedHandler(TaskManager manager) {
		super(manager);
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod();
		String path = exchange.getRequestURI().getPath();
		
		try {
			if ("GET".equals(method) && path.equals("/prioritized")) {
				handleGetPrioritizedTasks(exchange);
			} else {
				exchange.sendResponseHeaders(404, -1); // Если путь не найден
			}
		} catch (Exception e) {
			e.printStackTrace();
			exchange.sendResponseHeaders(500, -1); // Общая ошибка сервера
		}
	}
	
	// Метод для получения приоритезированных задач
	private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
		TreeSet<Task> prioritizedTasks = getTaskManager().getPrioritizedTasks();
		
		if (prioritizedTasks.isEmpty()) {
			exchange.sendResponseHeaders(204, -1); // Нет контента, задач нет
		} else {
			String response = toJson(prioritizedTasks); // Преобразование в JSON
			sendResponse(exchange, 200, response);
		}
	}
}
