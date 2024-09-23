package Manager.Impl;

import Enums.TaskStatus;
import Enums.TypeTask;
import Models.Epic;
import Models.Subtask;
import Models.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String pathToFile = System.getProperty("user.home") + File.separator + "tasks.csv";

    public FileBackedTaskManager(File existFile) {
        checkFile(existFile);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }


    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }


    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    public Epic addEpic(Epic epic) {
        super.createEpicTask(epic);
        save();
        return epic;
    }

    public Task addTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    private void checkFile(File existFile) {
        if (!existFile.exists()) {
            System.out.println("Проверка файла на существование");
            Path path = Paths.get(pathToFile);
            try {
                Files.createDirectories(path.getParent());
                if (!Files.exists(path))
                    System.out.println("Файл не обнаружен");
            } catch (IOException e) {
                System.out.println("Не удалось создать директорию или файл не обнаружен");
                System.exit(-1);
            }
        }
        load();
    }

    private void load() {
        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, Epic> epicTasks = new HashMap<>();
        HashMap<Integer, Subtask> subTasks = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ID")) continue;
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                String name = parts[2];
                String status = parts[3];
                String description = parts[4];
                int epicId = 0;
                if (parts.length > 5 && !parts[5].isEmpty())
                    epicId = Integer.parseInt(parts[5]);

                if (type.equals(TypeTask.TASK.name())) {
                    Task task = new Task(id, name, TaskStatus.valueOf(status), description);
                    tasks.put(task.getId(), task);
                }

                if (type.equals(TypeTask.SUBTASK.name())) {
                    Subtask subtask = new Subtask(id, name, TaskStatus.valueOf(status), description, epicId);
                    subTasks.put(subtask.getId(), subtask);
                }
                if (type.equals(TypeTask.EPIC.name())) {
                    ArrayList<Subtask> subtaskList = new ArrayList<>();
                    for (Subtask subtask : subTasks.values()) {
                        if (subtask.getEpicId() == id) subtaskList.add(subtask);
                    }
                    Epic subtask = new Epic(id, name, TaskStatus.valueOf(status), description, subtaskList);
                    epicTasks.put(subtask.getId(), subtask);
                }
                setTasks(tasks);
                setSubTasks(subTasks);
                setEpicTasks(epicTasks);
            }
        } catch (IOException e) {
            System.out.println("Не удалось загрузить задачи из файла: " + e.getMessage());
        }
    }

    private void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile))) {
            writer.write("ID,TYPE,NAME,STATUS,DESCRIPTION,EPIC\n");
            for (Map.Entry<Integer, Task> task : getTasks().entrySet()) {
                writer.write(task.getKey() + "," + TypeTask.TASK.toString() + "," + task.getValue().getName() + "," + task.getValue().getStatus() + "," + task.getValue().getDescription() + "," + ",\n");
            }
            for (Map.Entry<Integer, Epic> task : getEpicTasks().entrySet()) {
                writer.write(task.getKey() + "," + TypeTask.EPIC.toString() + "," + task.getValue().getName() + "," + task.getValue().getStatus() + "," + task.getValue().getDescription() + "," + ",\n");
            }
            for (Map.Entry<Integer, Subtask> task : getSubTasks().entrySet()) {
                writer.write(task.getKey() + "," + TypeTask.SUBTASK.toString() + "," + task.getValue().getName() + "," + task.getValue().getStatus() + "," + task.getValue().getDescription() + "," + task.getValue().getEpicId() + ",\n");
            }

        } catch (IOException e) {
            System.out.println("Failed to save tasks to file: " + e.getMessage());
        }
    }

}
