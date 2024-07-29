package Service.Impl;

import Service.UserInterface;

import java.util.Scanner;

public class UserInterfaceImpl implements UserInterface {
    private final String setPlainText = "\033[0;0m";
    private final String setBoldText = "\033[0;1m";

    @Override
    public void inputNewTask() {

    }

    @Override
    public void inputSubTask() {

    }

    @Override
    public void inputRemoveTask() {

    }

    @Override
    public void inputRemoveSubTask() {

    }

    @Override
    public String printTasksHeader() {
        return setBoldText + "||      TODO        ||     IN_PROGRESS    ||    DONE       ||" + setBoldText;
    }


    @Override
    public void printSubTasks() {

    }

    @Override
    public void printError(String message) {

    }

    @Override
    public void printMenuTask() {
        System.out.println("Список задач:");
        System.out.println("1. Показать задачи");
        System.out.println("2. Поменять статус подзадачи");
        System.out.println("3. Поменять тип задачи");
        System.out.println("4. Добавить задачу");
        System.out.println("5. Добавить подзадачу");
        System.out.println("6. Удалить подзадачу");
        System.out.println("7. Удалить задачу");
        System.out.println("8. Показать подзадачи");
        System.out.println("0. Выход");
    }

    @Override
    public String inputCommand() {
        Scanner scanner = new Scanner(System.in);
        String inputCommand = scanner.nextLine();
        scanner.close();
        return inputCommand;
    }

    @Override
    public void doCommand(String inputCommand) {
        switch (inputCommand) {
            case "1":
                System.out.println(printTasksHeader());
                break;
            case "2":
                inputSubTask();
                break;
            case "3":
                inputRemoveTask();
                break;
            case "4":
                inputNewTask();
                break;
            case "5":
                inputSubTask();
                break;
            case "6":
                inputRemoveSubTask();
                break;
            case "7":
        }
    }
}
