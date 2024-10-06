package Models;

import Enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
	
	protected Integer id;
	protected String name;
	protected String description;
	protected TaskStatus status;
	private LocalDateTime startTime; // дата и время начала задачи
	private Duration duration; // продолжительность задачи
	
	public Task(String name, String description) {
		this.name = name;
		this.description = description;
		this.status = TaskStatus.NEW;
	}
	
	public Task(Integer id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.status = TaskStatus.NEW;
	}
	
	public Task(Integer id, String name, TaskStatus status, String description) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.description = description;
		this.status = TaskStatus.NEW;
	}
	
	public Task(Task task) {
		this.id = task.id;
		this.name = task.name;
		this.description = task.description;
		this.status = task.status;
	}
	
	public Task(int id, String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime) {
		this.id = id;
		this.name = name;
		this.status = taskStatus;
		this.description = description;
		this.startTime = startTime;
		this.duration = duration;
	}
	
	// Метод для получения даты и времени завершения задачи
	public LocalDateTime getEndTime() {
		if (startTime != null && duration != null) {
			return startTime.plus(duration);
		}
		return null;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public TaskStatus getStatus() {
		return status;
	}
	
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	
	public LocalDateTime getStartTime() {
		return startTime;
	}
	
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	
	public Duration getDuration() {
		return duration;
	}
	
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	
	@Override
	public String toString() {
		return "Task{" +
			   "id=" + id +
			   ", name='" + name + '\'' +
			   ", description='" + description + '\'' +
			   ", status=" + status +
			   '}';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Task task = (Task) o;
		return Objects.equals(id, task.id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
	
}