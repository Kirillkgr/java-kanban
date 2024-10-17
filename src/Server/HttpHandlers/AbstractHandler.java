package Server.HttpHandlers;

import Manager.Impl.FileBackedTaskManager;
import Manager.TaskManager;
import Server.Adapter.DurationTypeAdapter;
import Server.Adapter.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;


public abstract class AbstractHandler implements HttpHandler {
	private final FileBackedTaskManager taskManager;
	private static Gson gson;
	
	public AbstractHandler(TaskManager taskManager) {
		this.taskManager = (FileBackedTaskManager) taskManager;
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
		gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
		gson = gsonBuilder.create();
	}
	
	public static void sendResponse(HttpExchange exchange, int codeResponse, String response) throws IOException {
		exchange.sendResponseHeaders(codeResponse, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
	
	protected static String toJson(Object obj) {
		return gson.toJson(obj);
	}
	
	protected <T> T fromJson(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}
	
	public TaskManager getTaskManager() {
		return taskManager;
	}
	
}
