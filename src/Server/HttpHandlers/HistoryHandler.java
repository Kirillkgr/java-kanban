package Server.HttpHandlers;

import Manager.TaskManager;
import Models.Task;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends AbstractHandler {
	
	public HistoryHandler(TaskManager manager) {
		super(manager);
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String method = exchange.getRequestMethod();
		String path = exchange.getRequestURI().getPath();
		
		try {
			if ("GET".equals(method) && path.equals("/history")) {
				handleGetHistory(exchange);
			} else {
				exchange.sendResponseHeaders(404, -1); // Если путь не найден
			}
		} catch (Exception e) {
			e.printStackTrace();
			exchange.sendResponseHeaders(500, -1); // Общая ошибка сервера
		}
	}
	
	// Метод для получения истории задач
	private void handleGetHistory(HttpExchange exchange) throws IOException {
		List<Task> history = getTaskManager().getHistory();
		
		if (history.isEmpty()) {
			exchange.sendResponseHeaders(204, -1); // Нет контента, история пустая
		} else {
			String response = toJson(history); // Преобразование в JSON
			sendResponse(exchange, 200, response);
		}
	}
}
