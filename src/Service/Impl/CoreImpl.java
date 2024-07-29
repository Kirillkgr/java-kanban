package Service.Impl;

import Service.Core;
import Service.UserInterface;

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
}
