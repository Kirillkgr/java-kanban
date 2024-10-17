package Server;

import Exeptions.ServerException;
import Manager.Impl.FileBackedTaskManager;
import Manager.TaskManager;
import Server.HttpHandlers.SubtaskHandler;
import Server.HttpHandlers.TaskHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
	private final HttpServer httpServer;
	private static final int PORT = 8080;
	
	public HttpTaskServer() {
		TaskManager taskManager = new FileBackedTaskManager(new File(System.getProperty("user.home") + File.separator + "tasks.csv"));
		try {
			httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
			
			httpServer.createContext("/tasks", new TaskHandler(taskManager));
			httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
//			httpServer.createContext("/epics", new EpicsHandler());
//			httpServer.createContext("/history", new HistoryHandler());
//			httpServer.createContext("/prioritized", new PrioritizedHandler());
			
			
		} catch (IOException e) {
			throw new ServerException("Не удалось создать HTTP-сервер");
		}
	}
	
	public void start() {
		httpServer.start();
		System.out.println("HTTP-сервер запущен");
	}
	
	public void stop() {
		httpServer.stop(0);
		System.out.println("HTTP-сервер остановлен");
	}
}
