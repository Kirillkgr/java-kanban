package Models;

import Enums.TaskStatus;
import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
	private int epicId;
	
	public Subtask(String name, String description, int epicId) {
		super(name, description);
		this.epicId = epicId;
	}
	
	public Subtask(Integer id, String name, TaskStatus status, String description, int epicId) {
		super(id, name, status, description);
		this.epicId = epicId;
	}
	
	public Subtask(Subtask subtask) {
		super(subtask.name, subtask.description);
		this.epicId = subtask.epicId;
	}
	
	public Subtask(int id, String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime, int epicId) {
		super(id, name, description, taskStatus, duration, startTime);
		this.epicId = epicId;
	}
	
	public Subtask(String name, String description, int epicId, Duration duration, LocalDateTime startTime) {
		super(name, description, TaskStatus.NEW, duration, startTime);
		this.epicId = epicId;
	}
	
	public int getEpicId() {
		return epicId;
	}
	
	@Override
	public String toString() {
		return "  Subtask { " +
			   "SubTaskId = " + id +
			   ", EpicId = " + epicId + "\n" +
			   ", Name = " + name + "\n" +
			   ", Description = " + description + "\n" +
			   ", Status = " + status + "\n" +
			   "-------------------------------------\n" +
			   '}';
	}
	
	public void setEpicId(Integer epicId) {
		this.epicId = epicId;
	}
}