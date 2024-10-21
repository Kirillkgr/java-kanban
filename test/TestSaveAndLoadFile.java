

import Models.Epic;
import Models.Task;
import Enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import Manager.Impl.FileBackedTaskManager;

import java.io.File;
import java.util.Map;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSaveAndLoadFile {

    private FileBackedTaskManager manager;
    private static File tempFile;

    @BeforeEach
    void setUp() {
        try {
            tempFile = File.createTempFile("test_tasks", ".csv");
            manager = new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            System.out.println("НЕ удалось создать временный файл");
        }
    }

    @AfterEach
    void tearDown() {
        manager.removeAllTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpicTasks();
        tempFile.deleteOnExit();
    }

    @Test
    @DisplayName("Создание и сохранение задачи")
    public void shouldCreateAndSaveTask() {
        Task task = new Task(1, "Task1", TaskStatus.NEW, "Description task1");
        manager.addTask(task);

        Map<Integer, Task> tasks = manager.getTasks();
        assertEquals(1, tasks.size(), "Количество задач не совпадает");
        assertTrue(tasks.containsKey(1), "Задача не была добавлена");
    }

    @Test
    @DisplayName("Создание и сохранение эпика")
    public void shouldCreateAndSaveEpic() {
        manager.removeAllTasks();
        manager.removeAllSubTasks();
        manager.removeAllEpicTasks();
        Epic epic = new Epic(1, "Epic1", TaskStatus.NEW, "Description epic1", new ArrayList<>());
        manager.addEpic(epic);

        Map<Integer, Epic> epics = manager.getEpicTasks();
        assertEquals(1, epics.size(), "Количество эпиков не совпадает");
        assertTrue(epics.containsKey(1), "Эпик не был добавлен");
    }

    @Test
    @DisplayName("Загрузка задач из файла")
    public void shouldLoadTasksFromFile() {

        Task task = new Task(1, "Task1", TaskStatus.NEW, "Description task1");
        Epic epic = new Epic(2, "Epic1", TaskStatus.NEW, "Description epic1", new ArrayList<>());
        manager.addTask(task);
        manager.addEpic(epic);


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);


        assertEquals(manager.getTasks(), loadedManager.getTasks(), "Задачи не совпадают");
        assertEquals(manager.getEpicTasks(), loadedManager.getEpicTasks(), "Эпики не совпадают");
    }

    @Test
    @DisplayName("Проверка идентичности содержимого менеджеров")
    public void shouldBeIdenticalAfterLoadFromFile() {

        Task task = new Task(1, "Task1", TaskStatus.NEW, "Description task1");
        Epic epic = new Epic(2, "Epic1", TaskStatus.NEW, "Description epic1", new ArrayList<>());
        manager.addTask(task);
        manager.addEpic(epic);


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);


        assertEquals(manager.getTasks(), loadedManager.getTasks(), "Задачи не идентичны");
        assertEquals(manager.getEpicTasks(), loadedManager.getEpicTasks(), "Эпики не идентичны");
    }
}