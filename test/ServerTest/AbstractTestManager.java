package ServerTest;

import Manager.TaskManager;
import Server.Adapter.DurationTypeAdapter;
import Server.Adapter.LocalDateTimeTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class AbstractTestManager {
	private static Gson gson;
	private TaskManager taskManager;
	
	public AbstractTestManager(TaskManager taskManager) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
		gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
		gson = gsonBuilder.create();
		this.taskManager = taskManager;
	}
	
	public TaskManager getTaskManager() {
		return this.taskManager;
	}
	
	public static void sendResponse(HttpExchange exchange, int codeResponse, String response) throws IOException {
		byte[] resp = response.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
		exchange.sendResponseHeaders(codeResponse, resp.length);
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
	
	protected <T> T fromJsonArray(String json, Type typeOfT) {
		return gson.fromJson(json, typeOfT);
	}
}
