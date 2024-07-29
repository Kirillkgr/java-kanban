package test;

import main.Service.Core;
import main.Service.Impl.CoreImpl;
import org.junit.Test;


public class MainTest {
    @Test
    public void test() {
        System.out.println("Test");
        Core core = new CoreImpl();
        core.startWithOutputInput();
    }
}
