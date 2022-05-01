import java.util.List;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashMap;
import java.math.BigInteger;
import java.io.*;


public class Node implements Runnable{
    List<Broker> brokers;

    private static File currDirectory = new File(new File(".").getAbsolutePath());
    private static String currDirectoryPath = currDirectory.getAbsolutePath().substring(0,currDirectory.getAbsolutePath().length() - 1);
    private static String homePath = currDirectory + "Database\\";
    private static String brokerstxt = homePath + "Brokers.txt";
    private static String topicsPath = homePath + "Topics.txt";
    private static String publishersTopics = homePath + "PublishersTopics.txt";
    private  ArrayList<Topic> topicsList= new ArrayList<>();
    private  ArrayList<Broker> brokersList= new ArrayList<>();

    void connect(){

    }
    void disconnect(){

    }

    void init (int port){

    }
    void updateNodes(){

    }

    public  ArrayList<Broker> loadBrokers(){
        BufferedReader buffReader;
        FileReader fReader;
        String line;
        String brokerName,ip;
        int port;
        try
        {
            fReader = new FileReader(brokerstxt);
            buffReader = new BufferedReader(fReader);



            while ((line = buffReader.readLine()) != null) {
                String[] token = line.split(",");

                brokerName = token[0].trim();
                ip = token[1].trim();
                port = Integer.parseInt(token[2].trim());
                Broker broker = new Broker(brokerName, ip, port);
                brokersList.add(broker);

            }
            buffReader.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return brokersList;
    }

    public  ArrayList<String> readPublisherTopics(String name){
        BufferedReader buffer;
        FileReader fr;
        String line;
        String pubName;
        String list;
        ArrayList<String> lTPub= new ArrayList<>() ;
        try
        {
            fr = new FileReader(publishersTopics);
            buffer = new BufferedReader(fr);



            while ((line = buffer.readLine()) != null) {
                String[] token = line.split(":");

                pubName = token[0].trim();
                list = token[1].trim();
                token = list.split(",");
                for(int i=0;i<token.length;i++){
                    lTPub.add(token[i]);
                }
            }
            buffer.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lTPub;
    }

    //ok
    public void readtopicsList(){
        FileReader fReader;
        BufferedReader buffReader;
        String row;
        String topicName;

        try{
            fReader = new FileReader(topicsPath);
            buffReader = new BufferedReader(fReader);

            while( (row = buffReader.readLine() ) != null) {
                //xwrise thn grammh ana keno (alla kathe grammi exei mono ena topic==Mathima
                String[] rowArray = row.split(" ");
                topicName = rowArray[0];
                Topic topic = new Topic(topicName);
                boolean existingTopic= false;
                if(!topicsList.isEmpty()){
                    for(Topic t : topicsList){
                        if(t.getChannelName().equals(topicName) ){
                            existingTopic = true;
                            break;
                        }
                    }       //an den yparxei hdh sth lista, prosthese to twra
                    if(existingTopic == false){
                        topicsList.add(topic);
                    }
                }
                else {  //an ayto einai to prwto topic pou tha mpei sth lista
                    topicsList.add(topic);
                }
                //edw an theloume mporoume na vazoume ta files pou exoun perasei
                //logika den xreiazetai
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
/*
TRASH CODE
//MIGHT BE THE BASIC IDEA FOR init()
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("OK");
            //Lamda expression to implement runnable
            Runnable receiverLoopSubs = () -> {
                while (true) {
                    try {
                        Socket s = serverSocket.accept();
                        System.out.println("OK");
                        Consumer.ConsumerHandler handler = new Consumer.ConsumerHandler(s);
                        // Create a new Thread with this object.
                        Thread t = new Thread(handler);
                        // start the thread.
                        t.start();
                    } catch (IOException e){
                        return;
                    }
                }
            };
            Thread serverThreadSub = new Thread(receiverLoopSubs);
            serverThreadSub.start();
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("Failed to initialize subscriber at port " + port + ": " + e.getMessage());
        }
        try {
            ServerSocket serverSocket = new ServerSocket(port + 1);
            Runnable receiverLoopPubs = () -> {
                while (true) {
                    try {
                        Socket s = serverSocket.accept();
                        Publisher.PublisherHandler handler = new Publisher.PublisherHandler(s);
                        // Create a new Thread with this object.
                        Thread t = new Thread(handler);
                        // start the thread.
                        t.start();
                    } catch (IOException e) {
                        return;
                    }
                }
            };
            Thread serverThreadPub = new Thread(receiverLoopPubs);
            serverThreadPub.start();
        } catch (IOException e) {
            System.err.println("Failed to initialize publisher at port " + port+1 + ": " + e.getMessage());
        }
        try {
            ServerSocket serverSocket = new ServerSocket(port + 2);
            Runnable receiverLoopBroker = () -> {
                while (true) {
                    try {
                        Socket s = serverSocket.accept();
                        Broker.BrokerHandler handler = new Broker.BrokerHandler(s);
                        // Create a new Thread with this object.
                        Thread t = new Thread(handler);
                        t.start();
                    } catch (IOException e) {
                        return;
                    }
                }
            };
            Thread serverThreadBroker = new Thread(receiverLoopBroker);
            serverThreadBroker.start();
        } catch (IOException e) {
            System.err.println("Failed to initialize broker at port " + port+2 + ": " + e.getMessage());
        }
 */
