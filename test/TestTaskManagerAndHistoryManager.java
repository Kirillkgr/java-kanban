import Manager.HistoryManager;
import Manager.Impl.InMemoryTaskManager;
import Manager.Managers;
import Models.Epic;
import Models.Subtask;
import Models.Task;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class TestTaskManagerAndHistoryManager {

    private InMemoryTaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void checkUpdateIdWhenCreateNewTask() { // проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        Task task1 = new Task("Task 1", "Description 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.createTask(task2);

        assertNotEquals(task1.getId(), task2.getId());

        assertEquals(task1.getId(), 1);
        assertEquals(task2.getId(), 2);

        Epic epic = new Epic("Test epic", "Test description");
        taskManager.createEpicTask(epic);
        assertEquals(epic.getId(), 3);

        Subtask subtask = new Subtask("Test subtask", "Test description", epic.getId());
         taskManager.createSubTask(subtask);
        assertEquals(subtask.getId(), 4);

    }

    @Test
    void testTasksEqualityById() { // проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    void testSubtaskEqualityById() { // проверьте, что наследники класса Task равны друг другу, если равен их id;
        Epic epic = new Epic("Epic 1", "Epic description");
        epic.setId(1);
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description", epic.getId());
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask description", epic.getId());
        subtask2.setId(1);

        assertEquals(subtask1, subtask2);
    }

    @Test
    void testEpicCannotBeAddedAsItsOwnSubtask() { // проверьте, что нельзя добавить задачу как подзадачу
        Epic epic = new Epic("Epic 1", "Epic description");
        epic.setId(1);

        assertNull(taskManager.createSubTask(new Subtask("Subtask", "Description", epic.getId())));
    }

    @Test
    void testSubtaskCannotBeItsOwnEpic() { //проверьте, что объект Subtask нельзя сделать своим же эпиком;
        Epic epic = new Epic("Epic 1", "Epic description");
        epic.setId(1);
        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());
        subtask.setId(1);

        assertNotEquals(taskManager.createEpicTask(epic), null
        );
    }

    @Test
    void testManagerAlwaysReturnsInitializedManagers() {
        assertNotNull(taskManager.getAllTasks());
        assertNotNull(taskManager.getAllEpics());
        assertNotNull(taskManager.getAllSubtasks());
    }

    @Test
    void testTaskManagerAddsAndFindsTasksById() {
        Task task = new Task("Task 1", "Description 1");
        taskManager.createTask(task);

        assertNotNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    void testTasksWithGeneratedAndSpecifiedIdDoNotConflict() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2");
        taskManager.createTask(task2);

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void testTaskImmutabilityWhenAddedToManager() {
        Task task = new Task("Task 1", "Description 1");
        taskManager.createTask(task);
        
        Optional<Task> retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask);
        assertEquals(task.getName(), retrievedTask.get().getName());
    }

    @Test
    void testHistoryManagerStoresPreviousTaskVersions() {
        Task task = new Task("Task 1", "Description 1");
        taskManager.createTask(task);
        historyManager.add(task);

        task.setName("Updated Task 1");
        taskManager.updateTask(task);
        
        Optional<Task>  actualTask = taskManager.getTaskById(task.getId());
        Task fromHistory = historyManager.getHistory().getFirst();
        assertEquals(actualTask.get().getId(), fromHistory.getId());
        assertEquals("Description 1", fromHistory.getDescription());
    }
}