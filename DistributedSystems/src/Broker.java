import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.math.BigInteger;
import java.io.*;

public class Broker implements Runnable {
    // push(topic,value) -> [broker]
    // pull(topic,[broker]) -> [topic,value]

    static List<Consumer> enrolledConsumers = new ArrayList<Consumer>();
    static List<Publisher> enrolledPublishers = new ArrayList<Publisher>();
    private DatagramSocket datagramSocket;
    private byte[] buffer = new byte[256];
    String topic;

    // brokers list must be static because they are the same, no matter the instance
    private static List<Broker> allBrokers;
    private String name, ip;
    private int port;
    private long bHashValue;
    ServerSocket serverSocket;
    Socket client;
    private List<Consumer> registeredConsumers;
    List<Topic> linkedTopics = new ArrayList<Topic>();; // hashmap == queue
    public HashMap<Topic, Queue<String>> topicsQueue = new HashMap<>();

    private static List<Topic> allTopics; // all topics of all brokers
    private static List<Consumer> allConsumers = new ArrayList<Consumer>();
    private static List<Publisher> allPublishers = new ArrayList<Publisher>();
    /*
     * public static void main(String args[]) throws IOException {
     * ServerSocket sSocket = new ServerSocket(4321);
     * Broker server = new Broker(sSocket);
     * server.pull("Hello");
     * }
     */

    public Broker() {
    }

    public Broker(String name, String ip, int port) {

        this.name = name;
        this.ip = ip;
        this.port = port;
        // this.enrolledPublishers = new ArrayList<>();
        // this.enrolledConsumers = new ArrayList<>();
        // this.linkedTopics = new ArrayList<>();
        // this.topicsQueue = new HashMap<>();
        // init();
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
        this.datagramSocket = datagramSocket;
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
        // init();
        // for (Broker br : allBrokers) {
        // Broker br = allBrokers.get(0);
        // ip = br.getIp();
        // port = br.getPort();

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

    @Override
    public void run() {
        // ServerSocket sSocket = null;
        // try {
        // sSocket = new ServerSocket(4321);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // Broker server = new Broker(sSocket);
        // server.pull("Hello");
    }
}

/*
 * TRASH CODE THAT MIGHT BE USEFUL
 * //TCP
 * try {
 * while(true) {
 * ServerSocket server = new ServerSocket(1234);
 * Socket client = server.accept();
 * System.out.println("Consumer is connected!");
 * BrokerHandler handler = new BrokerHandler(client);
 * //handler.run();
 * Thread thread = new Thread(handler);
 * thread.start();
 * }
 * //registeredUsers.add(name);
 * //BufferedReader br = new BufferedReader(new
 * InputStreamReader(client.getInputStream()));
 * //String str = br.readLine();
 * //System.out.println("Client data : " + str);
 * } catch (IOException e){
 * e.printStackTrace();
 * }
 */
/*
 * ServerSocket server = new ServerSocket(1234);
 * Socket client = server.accept();
 * //BrokerHandler handler = new BrokerHandler(client);
 * //handler.run();
 * Node nd = new Node();
 * nd.init(1234);
 */

/*
 * try {
 * //in = new ObjectInputStream(client.getInputStream());
 * //out = new ObjectOutputStream(client.getOutputStream());
 * //out.flush();
 * out = new PrintWriter(client.getOutputStream(), true);
 * in = new BufferedReader(new InputStreamReader(client.getInputStream()));
 * BufferedReader br = new BufferedReader(new
 * InputStreamReader(client.getInputStream()));
 * String str = br.readLine();
 * System.out.println("Publisher data : " + str);
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 * /*
 * //TCP
 * try {
 * while(true) {
 * ServerSocket server = new ServerSocket(4321);
 * Socket client = server.accept();
 * System.out.println("Publisher is connected!");
 * BrokerHandler handler = new BrokerHandler(client);
 * handler.run();
 * Thread thread = new Thread(handler);
 * thread.start();
 * }
 * //BufferedReader br = new BufferedReader(new
 * InputStreamReader(client.getInputStream()));
 * //String str = br.readLine();
 * //System.out.println("Publisher data : " + str);
 * } catch (IOException e){
 * e.printStackTrace();
 * }
 */
/*
 * try {
 * providerSocket = new ServerSocket(1234);
 * while (true) {
 * Socket connection = providerSocket.accept();
 * Thread thread = new Thread((Runnable) connection);
 * thread.start();
 * }
 * } catch (IOException ioException) {
 * ioException.printStackTrace();
 * } finally {
 * try {
 * providerSocket.close();
 * } catch (IOException ioException) {
 * ioException.printStackTrace();
 * }
 * }
 */
