import Enums.TaskStatus;
import Manager.TaskTracker;
import Models.Epic;
import Models.Subtask;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskTracker tracker = new TaskTracker();

        Epic epic1 = new Epic(TaskTracker.getNewId(), "Epic 1", "Description of Epic 1");
        tracker.createTask(epic1);

        Subtask subtask1 = new Subtask(TaskTracker.getNewId(), "Subtask 1", "Description of Subtask 1", epic1.getId());
        Subtask subtask2 = new Subtask(TaskTracker.getNewId(), "Subtask 2", "Description of Subtask 2", epic1.getId());

        tracker.createTask(subtask1);
        tracker.createTask(subtask2);

        System.out.println("\nСоздалась тестовая задача с двумя под задачами\n" + tracker.getTaskById(epic1.getId()));
        Subtask subtask3 = new Subtask(TaskTracker.getNewId(), "Subtask 3", "Description of Subtask 3", epic1.getId());
        tracker.createTask(subtask3);
        System.out.println("\nДобавилась третия под задача\n" + tracker.getTaskById(epic1.getId()));

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        System.out.println("\nСменили тип первой под задачи на Done и второй на IN_Progress\n" + tracker.getTaskById(epic1.getId()));

        tracker.removeTaskById(subtask1.getId());
        tracker.removeTaskById(subtask3.getId());
        System.out.println("\nУдалили первый и третию подзадачу\n" + tracker.getTaskById(epic1.getId()));

        subtask2.setStatus(TaskStatus.DONE);
        tracker.updateTaskStatus(subtask2);
        System.out.println("\nСменили тип второй на DONE\n" + tracker.getTaskById(epic1.getId()));

        System.out.println("\nВывод всех задач\n");
        System.out.println(tracker.getAllTasks().toString());

        System.out.println("\nВывод всех Epic задачь\n" + tracker.getAllEpics());

        System.out.println("\nВывод всех Subtasks задачь\n" + tracker.getAllSubtasks());

        subtask1 = (Subtask) tracker.getAllSubtasks().getFirst();
        subtask1.setName("New name");
        subtask1.setId(3);
        subtask1.setDescription("new description");
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        tracker.updateTask(subtask1);
        System.out.println("\nОбновление задачи \n" + tracker.getTaskById(epic1.getId()));


        System.out.println("\nПолучение всех задач под задачь используя EpicId \n" + tracker.getSubtasksOfEpic(epic1.getId()));

        tracker.removeAllTasks();
        System.out.println("\nУдаление всех задачь \n" + tracker.getTaskById(epic1.getId()));
    }
}
