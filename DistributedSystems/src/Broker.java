import java.util.List;

public class Broker extends Thread {
    //push(topic,value) -> [broker]
    //pull(topic,[broker]) -> [topic,value]

    List<Consumer> registeredUsers;
    List<Publisher> registeredPublishers;

    Consumer acceptConnection(Consumer name){}
    Publisher acceptConnection(Publisher publisher){}
    void calculateKeys(){}
    void filterConsumers(String consumers){}
    void notifyBrokersOnChanges(){}
    void notifyPublishers(String publisher){}
    void pull(String pull){}
}
