import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import helpers.FileHelper;

public class Node {

    static List<Broker> brokers = new ArrayList<>();
    Broker broker = new Broker();
    ServerSocket serverSocket;
    Socket client;
    int num;
    String subject;
    String ip;
    int port;
    String brokerName;
    private  ArrayList<Topic> topicsList= new ArrayList<>() ;

    private static File currDirectory = new File(new File("").getAbsolutePath());
    private static String topicsPath = currDirectory + "\\distributed_systems\\DistributedSystems\\data\\Topics.txt";
    private static String brokersPath = currDirectory + "\\distributed_systems\\DistributedSystems\\data\\Brokers.txt";
    private static String publishersPath = currDirectory + "\\distributed_systems\\DistributedSystems\\data\\Publishers.txt";

    public ArrayList<Topic> readTopicsList() {
        //ArrayList<Topic> topics = new ArrayList<>();
        ArrayList<String> topicsLines = FileHelper.readFile(topicsPath);
        for (String line : topicsLines) {
            topicsList.add(new Topic(line));
        }
        return topicsList;
    }

    public List<Broker> loadBrokers() {
        ArrayList<String> brokersLines = FileHelper.readFile(brokersPath);
        for (String line : brokersLines) {
            String[] data = line.split(" , ");
            brokers.add(new Broker(data[0], data[1], Integer.parseInt(data[2])));
        }

        return brokers;
    }

    private void addPublishers() {
        /*
        ArrayList<String> publishersNames = FileHelper.readFile(publishersPath);
        for (String publisherName : publishersNames) {
            broker.enrolledPublishers.add(new Publisher(new ProfileName(publisherName)));
        }
        */
        ArrayList<String> publishersNames = FileHelper.readFile(publishersPath);
        HashMap<String, ArrayList<Value>> userVideoFilesMap = new HashMap<>();
        HashMap<String, Integer> subscribedConversations = new HashMap<>();
        for (String publisherName : publishersNames) {
            String[] data = publisherName.split(" , ");
            int i = 0;
            List<String> userConversations = new ArrayList<String>(Arrays.asList(data[1]));
            for (String conversations : userConversations){
                String[] data1 = conversations.split(" ");
                for(String data2 : data1) {
                    subscribedConversations.put(data2, ++i);
                }
            }
            List<String> userFiles = new ArrayList<String>(Arrays.asList(data[2]));
            for (String files : userFiles){
                String[] data3 = files.split(" ");
                for(String data4 : data3) {
                    subscribedConversations.put(data4, ++i);
                }
            }
            broker.enrolledPublishers.add(new Publisher(new ProfileName(data[0], userVideoFilesMap, subscribedConversations)));
        }
    }

    private void init() {
        addPublishers();

        System.out.println("Please enter your name:");
        Scanner myUserName = new Scanner(System.in);
        String username = myUserName.nextLine();
        Publisher publisher = new Publisher(new ProfileName(username));
        boolean flag = false;
        if (broker.enrolledPublishers == null) {
            System.out.println("Welcome " + username);
            broker.enrolledPublishers.add(publisher);
        }
        for (Publisher publ : broker.enrolledPublishers) {
            if (publ.equals(publisher)) {
                System.out.println("Welcome back " + username);
                flag = true;
                break;
            }
        }
        if (!flag) {
            System.out.println("Welcome " + username);
            broker.enrolledPublishers.add(publisher);
        }

        flag = false;
        System.out.println("Select a topic");
        Scanner myTopic = new Scanner(System.in);
        subject = myTopic.nextLine();
        Node n = new Node();
        ArrayList<Topic> topics = n.readTopicsList();

        for (Topic topic : topics) {
            if (topic.getChannelName().equals(subject)) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            FileHelper.writeFile(topicsPath, subject);
            topics.add(new Topic(subject));
        }
        checkIfSubscribed(username, subject);
        uploadFile(username, subject);
    }

    synchronized void connect() {
        broker.init();
        for (Broker br : loadBrokers()) {
            Thread t = new Thread(br);
            t.start();
        }

    }

    void checkIfSubscribed(String username, String subject) {
        ProfileName user = new ProfileName(username);

        user.getSubscribedConversations().put("DATA_BASE", 1);

        for (Map.Entry<String, Integer> entry : user.getSubscribedConversations().entrySet())
            num = entry.getValue();
        if (user.getSubscribedConversations().containsValue(subject)) {
            System.out.println("User subscribed to " + subject);
        } else {
            user.getSubscribedConversations().put(subject, num + 1);
            System.out.println("Welcome to the channel " + subject);
        }

    }

    void uploadFile(String username, String subject) {
        ProfileName pn = new ProfileName(username);
        Publisher publisher = new Publisher(pn);

        System.out.println("Do you want to upload a file to the conversation? Yes/No");
        Scanner in = new Scanner(System.in);
        String answer = in.nextLine();

        MultimediaFile mf = new MultimediaFile(answer);
        Value val = new Value(mf);
        ArrayList<Value> addValues = new ArrayList<>();
        addValues.add(val);
        pn.userVideoFilesMap.put(subject, addValues);

        if (answer.equals("Yes")) {
            connect();
            publisher.send(subject); //new Socket(ip, port)
            disconnect();
        } else {
            System.out.println("No problem!");
        }
    }

    void disconnect() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Topic> gettopicsList(){
        return this.topicsList;
    }


    public static void main(String[] args) {
        Node n = new Node();
        ArrayList<Topic> topics = n.readTopicsList();
        for (Topic topic : topics) {
            System.out.println(topic.getChannelName());
        }
        // n.loadBrokers();
        n.init();
    }
}
