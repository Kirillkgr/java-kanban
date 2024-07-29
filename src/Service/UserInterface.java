package Service;

public interface UserInterface {
    void inputNewTask();
    void inputSubTask();
    void inputRemoveTask();
    void inputRemoveSubTask();

    String printTasksHeader();
    void printSubTasks();

    void printError(String message);
    void printMenuTask();

    String inputCommand();

    void doCommand(String inputCommand);
}
