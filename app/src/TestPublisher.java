import java.io.IOException;
import java.net.UnknownHostException;

public class TestPublisher {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Publisher publisher = new Publisher();
        publisher.start();
    }
}
