import Service.Core;
import Service.Impl.CoreImpl;
import Service.Impl.TaskTrackerImpl;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Core core = new CoreImpl();
        core.start();
    }
}
