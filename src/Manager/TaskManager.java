package Manager;

import Models.Epic;
import Models.Subtask;
import Models.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManager {
	
	
	List<Task> getAllTasks();
	
	void removeAllTasks();
	
	void removeAllEpicTasks();
	
	void removeAllSubTasks();
	
	Optional<Task> getTaskById(Integer id);
	
	Task getEpicTaskById(int id);
	
	Optional<Subtask> getSubTaskById(int id);
	
	int createTask(Task task);
	
	Epic createEpicTask(Epic epic);
	
	boolean createSubTask(Subtask subtask);
	
	Task updateTask(Task updatedTask);
	
	void updateEpic(Epic epic);
	
	void removeEpicById(int id);
	
	boolean removeSubTaskById(int id);
	
	boolean removeTaskById(Integer id);
	
	List<Subtask> getSubtasksOfEpic(int epicId);
	
	List<Epic> getAllEpics();
	
	List<Subtask> getAllSubtasks();
	
	boolean updateSubtask(Subtask subtask);
	
	List<Task> getHistory();
	
	boolean isTaskTimeIntersect(Task newTask);
}
