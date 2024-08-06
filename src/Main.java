import Enums.TaskStatus;
import Manager.TaskTrackerManager;
import Models.Epic;
import Models.Subtask;
import Models.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskTrackerManager tracker = new TaskTrackerManager();


        Task task1 = new Task("Task 1", "Description task 1");
        Task task2 = new Task("Task 2", "Description task 2");
        tracker.addTask(task1);
        tracker.addTask(task2);

        System.out.println("\nСоздалась первая и  вторая простая задача\n" + tracker.getAllTasks());
        task1.setDescription("new Description");

        tracker.updateTask(task2);
        System.out.println("\nОбновилась вторая простая задача\n" + tracker.getAllTasks());

        tracker.deleteTask(task1);
        System.out.println("\nУдалилась первая простая задача\n" + tracker.getAllTasks());


        Epic epic1 = new Epic("Epic 1", "Description of Epic 1");
        epic1 = tracker.createEpicTask(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", epic1.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description of Subtask 3", epic1.getId());
        subtask1.setEpicId(epic1.getId());
        subtask2.setEpicId(epic1.getId());
        subtask3.setEpicId(epic1.getId());

        tracker.addSubtaskToEpic(subtask1);
        tracker.addSubtaskToEpic(subtask2);

        System.out.println("\nСоздалась тестовая задача с двумя под задачами\n" + tracker.getEpicTaskById(epic1.getId()));


        tracker.addSubtaskToEpic(subtask3);
        System.out.println("\nДобавилась третия под задача\n" + tracker.getEpicTaskById(epic1.getId()));

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);

        tracker.updateSubtask(subtask1);
        System.out.println("\nСменили тип первой под задачи на Done и второй на IN_Progress\n" + tracker.getEpicTaskById(epic1.getId()));

        tracker.deleteSubtask(subtask1);
        tracker.removeTaskById(subtask3.getId());
        System.out.println("\nУдалили первый и третию подзадачу\n" + tracker.getEpicTaskById(epic1.getId()));

        subtask2.setStatus(TaskStatus.DONE);
        tracker.updateSubtaskStatus(subtask2);
        System.out.println("\nСменили тип второй на DONE\n" + tracker.getEpicTaskById(epic1.getId()));

        Subtask subtask4 = new Subtask("Subtask 3", "Description of Subtask 3", epic1.getId());
        subtask4.setEpicId(epic1.getId());
        tracker.addSubtaskToEpic(subtask4);
        System.out.println("\nДобавилась четвертая под задача\n" + tracker.getEpicTaskById(epic1.getId()));

        subtask4.setDescription("This new description");
        tracker.updateSubtask(subtask4);
        System.out.println("\nИзменили описаание 4 под задачи\n" + tracker.getEpicTaskById(epic1.getId()));

        epic1.setDescription("This new description");
        epic1.updateStatus();
        System.out.println("\nИзменили изменили описание Epic\n" + tracker.getEpicTaskById(epic1.getId()));

        System.out.println("\nВывод всех задач типа NEW\n" + tracker.getListOfTasks(TaskStatus.NEW).toString());

        System.out.println("\nВывод всех задач типа IN_PROGRESS\n" + tracker.getListOfTasks(TaskStatus.IN_PROGRESS));

        System.out.println("\nВывод всех задач типа DONE\n" + tracker.getListOfTasks(TaskStatus.DONE));

        System.out.println("\nПолучение всех задач под задачь используя EpicId \n" + tracker.getSubtasksOfEpic(epic1.getId()));

        System.out.println("\nВывод всех задач\n" + (tracker.getAllTasks() == null ? "Список пуст" : tracker.getAllTasks()));

        System.out.println("\nВывод всех Epic задачь\n" + tracker.getAllEpics());

//        tracker.getAllSubtasks();
        System.out.println("\nВывод всех Subtasks задачь\n" + (tracker.getAllSubtasks() == null ? "Список под задачь пуст" : tracker.getAllSubtasks()));


        tracker.removeAllEpicTasks();
        System.out.println("\nУдаление всех Epic задачь \n" + tracker.getTaskById(epic1.getId()));

    }
}
