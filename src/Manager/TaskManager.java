package Manager;

import Models.Epic;
import Models.Subtask;
import Models.Task;

import java.util.List;

public interface TaskManager {
	
	
	List<Task> getAllTasks();
	
	void removeAllTasks();
	
	void removeAllEpicTasks();
	
	void removeAllSubTasks();
	
	Task getTaskById(int id);
	
	Task getEpicTaskById(int id);
	
	Task getSubTaskById(int id);
	
	void createTask(Task task);
	
	Epic createEpicTask(Epic epic);
	
	Subtask createSubTask(Subtask subtask);
	
	void updateTask(Task updatedTask);
	
	void updateEpic(Epic epic);
	
	void removeEpicById(int id);
	
	void removeSubTaskById(int id);
	
	void removeTaskById(int id);
	
	List<Subtask> getSubtasksOfEpic(int epicId);
	
	List<Epic> getAllEpics();
	
	List<Subtask> getAllSubtasks();
	
	void updateSubtask(Subtask subtask);
	
	List<Task> getHistory();
	
	
	boolean isSubtaskTimeIntersect(Subtask newSubtask);
	
	boolean isTaskTimeIntersect(Task newTask);
	
	boolean isTimeOverlap(Task task1, Task task2);
	
}
