import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class TestBroker {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        // 1 , 2, 3 are valid arguments
        ArrayList<Broker> brokers = LoadData.loadBrokers();
        Integer choice = Integer.parseInt(args[0]);
        if (choice != 1 && choice != 2 && choice != 3) {
            System.out.println("Invalid argument");
            System.exit(1);
        }
        brokers.get(choice - 1).start();
    }
}
