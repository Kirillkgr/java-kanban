import Manager.Impl.InMemoryHistoryManager;
import Models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestLinkedListOfHistoryInMemoryHistoryManager {

    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void setup() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void testAddTaskToHistory() {
        Task task1 = new Task(1, "Task 1","Description 1");
        Task task2 = new Task(2, "Task 2","Description 2");

        historyManager.add(task1);
        historyManager.add(task2);

        LinkedList<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать 2 задания");
        assertEquals(task1, history.get(0), "Первой задачей в истории должна быть Задача 1.");
        assertEquals(task2, history.get(1), "Второй задачей в истории должна стать Задача 2.");
    }

    @Test
    public void testAddDuplicateTaskReplacesPrevious() {
        Task task1 = new Task(1, "Task 1","Description 1");
        Task task2 = new Task(2, "Task 2","Description 2");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);  // Adding the same task again

        LinkedList<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain 2 tasks after adding duplicate");
        assertEquals(task2, history.get(0), "First task in history should be Task 2 after adding duplicate");
        assertEquals(task1, history.get(1), "Second task in history should be Task 1 after adding duplicate");
    }

    @Test
    public void testRemoveTaskFromHistory() {
        Task task1 = new Task(1, "Task 1","Description 1");
        Task task2 = new Task(2, "Task 2","Description 2");
        Task task3 = new Task(3, "Task 3","Description 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2); // Removing task 2

        LinkedList<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain 2 tasks after removal");
        assertFalse(history.contains(task2), "Task 2 should be removed from history");
    }

    @Test
    public void testGetHistoryOrder() {
        Task task1 = new Task(1, "Task 1","Description 1");
        Task task2 = new Task(2, "Task 2","Description 2");
        Task task3 = new Task(3, "Task 3","Description 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "History should contain 3 tasks");
        assertEquals(task1, history.get(0), "First task in history should be Task 1");
        assertEquals(task2, history.get(1), "Second task in history should be Task 2");
        assertEquals(task3, history.get(2), "Third task in history should be Task 3");
    }
}