package main.Service.Impl;

import main.Service.Core;
import main.Service.UserInterface;

public class CoreImpl implements Core {
    ;
    @Override
    public void start() {
        UserInterface userInterface = new UserInterfaceImpl();
        userInterface.printMenuTask();
        String inputCommand =userInterface.inputCommand();
        userInterface.doCommand(inputCommand);
        System.out.println("Start");
    }

    @Override
    public void startWithOutputInput() {
        UserInterface userInterface = new UserInterfaceImpl();
        userInterface.printMenuTask();
//        String inputCommand =userInterface.inputCommand();
//        userInterface.doCommand(inputCommand);
        System.out.println("Start tes   ");
    }
}
