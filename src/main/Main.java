package main;

import main.Service.Core;
import main.Service.Impl.CoreImpl;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Core core = new CoreImpl();
        core.start();
    }
}
