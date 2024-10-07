import Manager.Impl.FileBackedTaskManager;
import Models.Task;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {
	private FileBackedTaskManager manager;
	private final File testFile = new File("test_tasks.csv");
	
	@BeforeEach
	public void setUp() {
		manager = new FileBackedTaskManager(testFile);
	}
	
	@Test
	public void testSaveAndLoadTasks() {
		Task task1 = new Task("Task 1", "Description 1");
		task1.setDuration(Duration.ofMinutes(30));
		task1.setStartTime(LocalDateTime.of(2024, 10, 1, 10, 0));
		
		manager.addTask(task1);
		manager.save();
		
		FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(testFile);
		Task loadedTask = loadedManager.getTasks().get(task1.getId());
		
		assertNotNull(loadedTask);
		assertEquals(task1.getName(), loadedTask.getName());
		assertEquals(task1.getStartTime(), loadedTask.getStartTime());
		assertEquals(task1.getDuration(), loadedTask.getDuration());
	}
	
	@Test
	public void testSortTasksByStartTime() {
		Task task1 = new Task("Task 1", "Description 1");
		task1.setStartTime(LocalDateTime.of(2024, 10, 1, 10, 0));
		
		Task task2 = new Task("Task 2", "Description 2");
		task2.setStartTime(LocalDateTime.of(2024, 10, 1, 9, 0));
		
		manager.addTask(task1);
		manager.addTask(task2);
		
		TreeSet<Task> sortedTasks = manager.getPrioritizedTasks();
		assertEquals(task2, sortedTasks.getFirst()); // task2 должен быть первым
		assertEquals(task1, sortedTasks.getLast()); // task1 должен быть вторым
	}
	
	@Test
	public void testTaskTimeIntersection() {
		Task task1 = new Task("Task 1", "Description 1");
		task1.setStartTime(LocalDateTime.of(2024, 10, 1, 10, 0));
		task1.setDuration(Duration.ofMinutes(30));
		manager.createTask(task1);
		
		Task task2 = new Task("Task 2", "Description 2");
		task2.setStartTime(LocalDateTime.of(2024, 10, 1, 10, 15)); // пересекается с task1
		task2.setDuration(Duration.ofMinutes(30));
		manager.createTask(task2);
		
		assertTrue(manager.isTaskTimeIntersect(task2));
	}
}
