package Models;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
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
}