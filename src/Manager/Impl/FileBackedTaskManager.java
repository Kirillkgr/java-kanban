package Manager.Impl;

import Manager.HistoryManager;
import Models.Epic;
import Models.Subtask;
import Models.Task;

import java.io.File;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String pathToFile = System.getProperty("(user.home") + File.separator + "tasks.csv";
    private File actualFile;

    FileBackedTaskManager(File existFile) {
        if (existFile.exists()) {
            actualFile = existFile;
            load();
        } else {
            actualFile = new File(pathToFile);
        }
    }

    private void load() {
        // TODO: реализовать загрузку задач из файла
    }
    private void save() {
        // TODO: реализовать сохранение задач в файл
        /*id,type,name,status,description,epic
          1,TASK,Task1,NEW,Description task1,
          2,EPIC,Epic2,DONE,Description epic2,
          3,SUBTASK,Sub Task2,DONE,Description sub task3,2*/
    }


    @Override
    public void updateEpic(Epic epic) {
        save();
        super.updateEpic(epic);
    }

    @Override
    public void removeEpicById(int id) {
        save();
        super.removeEpicById(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        save();
        super.removeSubTaskById(id);
    }

    @Override
    public void removeTaskById(int id) {
        save();
        super.removeTaskById(id);
    }


    public void updateSubtask(Subtask subtask) {
        save();
        super.updateSubtask(subtask);
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


}
