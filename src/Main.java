import Enums.TaskStatus;
import Manager.TaskTracker;
import Models.Epic;
import Models.Subtask;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskTracker tracker = new TaskTracker();

        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        tracker.addTask(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", epic1.getId());

        tracker.addTask(subtask1);
        tracker.addTask(subtask2);

        System.out.println(tracker.getTaskById(epic1.getId())+"\n");
        System.out.println(tracker.getTaskById(subtask1.getId())+"\n");
        System.out.println(tracker.getTaskById(subtask2.getId())+"\n");

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);

        epic1.updateStatus();
        System.out.println(tracker.getTaskById(epic1.getId()));

        subtask2.setStatus(TaskStatus.DONE);

        epic1.updateStatus();
        System.out.println(tracker.getTaskById(epic1.getId()));
    }
}
