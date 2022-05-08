import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class Broker {
    // push(topic,value) -> [broker]
    // pull(topic,[broker]) -> [topic,value]

    static List<Consumer> enrolledConsumers = new ArrayList<Consumer>();
    static List<Publisher> enrolledPublishers = new ArrayList<Publisher>();
    String topic;

    // brokers list must be static because they are the same, no matter the instance
    private static List<Broker> allBrokers;
    private String name, ip;
    private int port;
    private long bHashValue;
    ServerSocket serverSocket;
    Socket client;
    List<Topic> linkedTopics = new ArrayList<Topic>();; // hashmap == queue
    public HashMap<Topic, Queue<String>> topicsQueue = new HashMap<>();

    private static List<Topic> allTopics; // all topics of all brokers

    public Broker() {
    }

    public Broker(String name, String ip, int port) {

        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public synchronized void init() {
        Node n = new Node();
        // n.readRouteCodes();
        allBrokers = n.loadBrokers();
        allTopics = n.gettopicsList();
        // setTopics(n);
        // setPubOwnTopics(this.name);
        // settopicsQueue(n);
        connectToBroker();
    }

    public Broker(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Broker(String topic) {
        this.topic = topic;
    }

    public Broker(DatagramSocket datagramSocket) {
    }

    public Broker(List<Consumer> enrolledConsumers, List<Publisher> enrolledPublishers) {
        this.enrolledConsumers = enrolledConsumers;
        this.enrolledPublishers = enrolledPublishers;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    Consumer acceptConnection(Consumer name) {
        for (Consumer registeredUser : enrolledConsumers) {
            if (registeredUser == name) {
                try {
                    client = serverSocket.accept();
                    System.out.println("Publisher is connected!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        enrolledConsumers.add(name);
        return name;
    }

    Publisher acceptConnection(Publisher publisher) {
        for (Publisher registeredPublisher : enrolledPublishers) {
            if (registeredPublisher == publisher) {
                try {
                    client = serverSocket.accept();
                    System.out.println("Publisher is connected!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        enrolledPublishers.add(publisher);
        return publisher;
    }

    void filterConsumers(String consumers) {
    }

    void notifyBrokersOnChanges() {
    }

    void notifyPublishers(String publisher) {
        for (Publisher registeredPublishers : enrolledPublishers) {
        }
    }

    synchronized void connectToBroker() {

        try {
            Broker br = new Broker(new ServerSocket(4321));
            // ip = br.getIp();
            // port = br.getPort();
            Socket client1 = new Socket("127.0.0.1", 4321);
            // ServerSocket serverSocket = new ServerSocket(port);
            // BrokerHandler handler = new BrokerHandler(client1);
            br.pull("Hello");
            // Thread t = new Thread(handler);
            // t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Thread t = new Thread(br);
        // t.start();
        // }
    }

    void pull(String brokerName) {

    }

    public void closeBroker() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Topic> getlinkedTopics() {
        return this.linkedTopics;
    }

    public List<Topic> getallTopics() {
        return allTopics;
    }

    public int getPort() {
        return this.port;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<Broker> getAllBrokers() {
        return allBrokers;
    }

    public String getBrokerName() {
        return this.name;
    }

    public Long getbHashValue() {
        return this.bHashValue;
    }

    public void setbHashValue(Long h) {
        this.bHashValue = h;
    }

}
