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
	protected Duration duration; // новое поле для продолжительности
	protected LocalDateTime startTime; // новое поле для времени начала
	
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
	}
	
	public Task(Task task) {
		this.id = task.id;
		this.name = task.name;
		this.description = task.description;
		this.status = task.status;
		this.duration = task.duration;
		this.startTime = task.startTime;
	}
	
	public Task(String name, String description, LocalDateTime startTime, int timeDurationInMinutes) {
		this.name = name;
		this.description = description;
		this.status = TaskStatus.NEW;
		this.startTime = startTime;
		this.duration = Duration.ofMinutes(timeDurationInMinutes);
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
	
	public Duration getDuration() {
		return duration;
	}
	
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	
	public LocalDateTime getStartTime() {
		return startTime;
	}
	
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	
	// Метод для вычисления времени завершения задачи
	public LocalDateTime getEndTime() {
		if (startTime != null && duration != null) {
			return startTime.plus(duration);
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "Task{" +
			   "id=" + id +
			   ", name='" + name + '\'' +
			   ", description='" + description + '\'' +
			   ", status=" + status +
			   ", startTime=" + startTime +
			   ", duration=" + duration +
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
		return Objects.hash(id);
	}
}
