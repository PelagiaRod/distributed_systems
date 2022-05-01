import java.util.*;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Node implements Runnable{
    List<Broker> brokers = new ArrayList<>();
    Broker broker = new Broker();
    Socket client;

    private  ArrayList<Topic> topicsList= new ArrayList<>();
    private  ArrayList<Broker> brokersList= new ArrayList<>();

    // these work
    private static File currDirectory = new File(new File("").getAbsolutePath());
    private static String topicsPath = currDirectory + "\\Topics.txt";



    void connect(){
        try {
            client = new Socket(broker.getIp(), broker.getPort());
            Publisher.PublisherHandler handler = new Publisher.PublisherHandler(client);
            Thread t = new Thread(handler);
            t.start();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    void disconnect(){
        try {
            if (client != null) {
                client.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    void init (int port){

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
            if(publ == publisher){
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
        String subject = in1.nextLine();
        Node n = new Node();
        ArrayList<Topic> topics = n.readtopicsList();
        for(Topic topic : topics){
            if(topic.equals(subject)) {
                flag = true;
                break;
            }
        }
        if (!flag){
            System.out.println("Topic does not exist");
        }


    }
    void updateNodes(){

    }

    public  List<Broker> loadBrokers(){    //ArrayList<Broker>

        brokers.add(new Broker("Broker1", "127.0.0.1", 4321));
        brokers.add(new Broker("Broker2", "127.0.0.1", 4322));
        brokers.add(new Broker("Broker3", "127.0.0.1", 4323));

        return brokers;


    }

    //ok
    public ArrayList<Topic> readtopicsList(){   //**was void
        //*tried this
        try{
            FileReader fReader = new FileReader(topicsPath);
            BufferedReader buffReader = new BufferedReader(fReader);

            String line = buffReader.readLine();
            while (line != null) {
                topicsList.add(new Topic(line));
                line = buffReader.readLine();
            }
            buffReader.close();

        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return topicsList;



    }

    public ArrayList<Topic> getTopicsList(){
        return this.topicsList;
    }
    public ArrayList<Broker> getAllBrokers(){
        return this.brokersList;
    }


    @Override
    public void run(){

    }

}
