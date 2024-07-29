package Models;

import Enums.TypeTask;

import java.util.List;
import java.util.UUID;

public class TaskModel {
    private String id = UUID.randomUUID().toString().replaceAll("-", "");
    private String name;
    private List<SubTaskModel> subTasks; // <subTaskName>
    private TypeTask typeTask;

    public void addSubTask(String newSubTaskName) {
        List<SubTaskModel> subTaskModels = this.subTasks; // <subTask>
        SubTaskModel subTaskModel = new SubTaskModel();
        subTaskModel.setMainId(this.id);
        subTaskModel.setName(newSubTaskName);
        subTasks.add(subTaskModel);
    }
    public void removeSubTask(SubTaskModel subTaskModel) {
        this.subTasks.remove(subTaskModel);
    }
    public void changeTypeTask(TypeTask typeTask) {
        this.typeTask = typeTask;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public List<SubTaskModel> getSubTasks() {
        return subTasks;
    }
    public TypeTask getTypeTask() {
        return typeTask;
    }
    public void setName(String name) {
        this.name = name;
    }
}
