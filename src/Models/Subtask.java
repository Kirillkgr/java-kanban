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
	
	public Subtask(String name, String description, Integer epicId, int durationMinutes) {
		super(name, description);
		this.epicId = epicId;
		this.duration = Duration.ofMinutes(durationMinutes);
		;
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