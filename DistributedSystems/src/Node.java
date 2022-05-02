import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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

    private static File currDirectory = new File(new File("").getAbsolutePath());
    private static String topicsPath = currDirectory + "\\Topics.txt";

    public ArrayList<Topic> readTopicsList(){
        ArrayList<Topic> listOfLines = new ArrayList<>();
        try{
            FileReader fReader = new FileReader(topicsPath);
            BufferedReader buffReader = new BufferedReader(fReader);

            String line = buffReader.readLine();
            while (line != null) {
                listOfLines.add(new Topic(line));
                line = buffReader.readLine();
            }
            buffReader.close();

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listOfLines;
    }

    public  List<Broker> loadBrokers() throws NoSuchAlgorithmException {
        brokers.add(new Broker("Broker1", "127.0.0.1", 4321));
        brokers.add(new Broker("Broker2", "127.0.0.1", 4322));
        brokers.add(new Broker("Broker3", "127.0.0.1", 4323));
        return brokers;
    }

    void init(){
        broker.registeredPublishers.add(new Publisher(new ProfileName("Gigi")));
        broker.registeredPublishers.add(new Publisher(new ProfileName("Paul")));

        System.out.println("Please enter your name:");
        Scanner in = new Scanner(System.in);
        String username = in.nextLine();
        Publisher publisher = new Publisher(new ProfileName(username));
        boolean flag = false;
        if(broker.registeredPublishers == null){
            System.out.println("Welcome " + username);
            broker.registeredPublishers.add(publisher);
        }
        for (Publisher publ : broker.registeredPublishers){
            if(publ.equals(publisher)){
                System.out.println("Welcome back " + username);
                flag = true;
                break;
            }
        }
        if (!flag) {
            System.out.println("Welcome " + username);
            broker.registeredPublishers.add(publisher);
        }

        flag = false;
        System.out.println("Select a topic");
        Scanner in1 = new Scanner(System.in);
        subject = in1.nextLine();
        Node n = new Node();
        ArrayList<Topic> topics = n.readTopicsList();
        for(Topic topic : topics){
            if(topic.getChannelName().equals(subject)) {
                flag = true;
                checkIfSubscribed(username, subject);
                uploadFile(username, subject);
                break;
            }
        }
        if (!flag){
            System.out.println("Topic does not exist");
            //TODO:ADD TOPIC
        }

    }

    void connect(){
        try {
            broker.init();
            broker.calculateKeys();
            for (Broker br : broker.getAllBrokers())
            {
                if (br.getQueueOfTopics().containsValue(new Topic(subject))){
                    ip = br.getIp();
                    port = br.getPort();
                    brokerName = br.getBrokerName();
                }
            }

            serverSocket = new ServerSocket(port);
            Broker server = new Broker(serverSocket);
            Thread t = new Thread(server);
            t.start();

            client = new Socket(ip, port);
            Publisher publ = new Publisher();
            server.acceptConnection(publ);
            Thread t1 = new Thread(publ);
            t1.start();

        } catch (IOException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    void checkIfSubscribed(String username, String subject){
        ProfileName user = new ProfileName(username);

        user.getSubscribedConversations().put("DATA_BASE", 1);

        for(Map.Entry<String, Integer> entry : user.getSubscribedConversations().entrySet())
            num = entry.getValue();
        if (user.getSubscribedConversations().containsValue(subject)){
            System.out.println("User subscribed to " + subject);
        } else {
            user.getSubscribedConversations().put(subject, num+1);
            System.out.println("Welcome to the channel " + subject);
        }

    }

    void uploadFile(String username, String subject){
        ProfileName pn = new ProfileName(username);
        Publisher publisher = new Publisher(pn);

        try {
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
                publisher.send(new Socket(ip, port));     //(subject);
                disconnect();
            } else {
                System.out.println("No problem!");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    void disconnect(){
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Node n = new Node();
        ArrayList<Topic> topics = n.readTopicsList();
        for(Topic topic : topics){
            System.out.println(topic.getChannelName());
        }
        //n.loadBrokers();
        n.init();
    }
}
