import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import helpers.FileHelper;

public class Node{


    static List<Broker> brokers = new ArrayList<>();
    Broker broker = new Broker();
    static ServerSocket serverSocket;
    Socket client;
    int num;
    String subject;
    static String ip;
    static int port;
    String brokerName;
    private static  ArrayList<Topic> topicsList= new ArrayList<>() ;
    Publisher publisher = new Publisher();
    static Vector<ClientHandler> ar = new Vector<>();


    private static File currDirectory = new File(new File("").getAbsolutePath());
    private static String topicsPath = currDirectory + "\\distributed_systems\\DistributedSystems\\data\\Topics.txt";
    private static String brokersPath = currDirectory + "\\distributed_systems\\DistributedSystems\\data\\Brokers.txt";
    private static String publishersPath = currDirectory + "\\distributed_systems\\DistributedSystems\\data\\Publishers.txt";


    public static ArrayList<Topic> readTopicsList() {
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


    private void hashOfBrokers() throws NoSuchAlgorithmException {
        for (Broker br : brokers) {
            int ipPlusPort = Integer.parseInt(br.getIp()) + br.getPort();
            String strIpPlusPort = Integer.toString(ipPlusPort);
            br.setbHashValue(hashCode(strIpPlusPort));
        }
    }

    private Long hashCode(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        return no.longValue();
    }


    public void calculateKeys() throws NoSuchAlgorithmException {
        // calculate each of the Brokers hash value
        hashOfBrokers();
        // calculate TopicsHash
        HashMap<String, Long> topicHashes = calculateTopicHash();
        List<Topic> copyTopics = new ArrayList<>();
        for (Topic t : topicsList) {
            copyTopics.add(t);
        }
        // compare topic hashes and broker hash value
        if (!topicHashes.isEmpty()) {
            ArrayList<Long> allBrokHash = allBrokerHash();
            if (allBrokHash != null) {
                // iterate the list of the values of the brokers' hash
                for (int i = 0; i < allBrokHash.size(); i++) {
                    for (Topic t : topicsList) {
                        if (copyTopics.indexOf(t) > -1) {
                            long h = topicHashes.get(t.getChannelName());
                            int s = allBrokHash.size() - 1;

                            if (i == 0) {
                                if ((h < allBrokHash.get(i)) || (h >= allBrokHash.get(s))) {
                                    for (Broker b : brokers) {
                                        if (b.getbHashValue() == allBrokHash.get(i)) {
                                            b.linkedTopics.add(t);
                                            int index = copyTopics.indexOf(t);
                                            copyTopics.remove(index);
                                        }
                                    }
                                }
                            } else if (h < allBrokHash.get(i) && h >= allBrokHash.get(i - 1)) {
                                for (Broker b : brokers) {
                                    if (b.getbHashValue() == allBrokHash.get(i)) {
                                        b.linkedTopics.add(t);
                                        int index = copyTopics.indexOf(t);
                                        copyTopics.remove(index);
                                    }
                                }
                            } else
                                continue;
                        }
                    }
                }
            }
        }
        for (Broker b : brokers) {
            // System.out.println(b.getName()+" "+b.getHash());
            HashMap<Topic, ArrayList<Queue<Value>>> q = new HashMap<>();
            for (Topic t : b.linkedTopics) {  //b.getlinkedTopics()
                // System.out.println(t.getBusLine());
                ArrayList<Queue<Value>> val = new ArrayList<>();
                ;
                q.put(t, val);
            }
            b.settopicsQueue(q);
        }

    }

    // returns list of brokers' hash
    private ArrayList<Long> allBrokerHash() {
        ArrayList<Long> allBrokHash = new ArrayList<>();
        if (!brokers.isEmpty()) {
            for (Broker b : brokers) {
                allBrokHash.add(b.getbHashValue());
            } // sort the hash to be in order
            Collections.sort(allBrokHash);
            return allBrokHash;
        } else {
            return null;
        }
    }


    private HashMap<String, Long> calculateTopicHash() throws NoSuchAlgorithmException {

        HashMap<String, Long> topicHashes = new HashMap<>();
        for (Topic t : topicsList) {
            long h = hashCode(t.getChannelName());
            topicHashes.put(t.getChannelName(), h);
        }
        return topicHashes;
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

    public static void main(String[] args) throws IOException {

        // Vector to store active clients


        // counter for clients
            int i = 0;
            // server is listening on port 1234
            serverSocket = new ServerSocket(1234);

            Socket s;

            // running infinite loop for getting
            // client request
            while (true)
            {
                // Accept the incoming request
                s = serverSocket.accept();

                System.out.println("New client request received : " + s);

                // obtain input and output streams
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Creating a new handler for this client...");

                // Create a new handler object for handling this request.
                ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos);

                // Create a new Thread with this object.
                Thread t = new Thread(mtch);

                System.out.println("Adding this client to active client list");

                // add this client to active clients list
                ar.add(mtch);

                // start the thread.
                t.start();

                // increment i for new client.
                // i is used for naming only, and can be replaced
                // by any naming scheme
                i++;


        }
    }







    // ClientHandler class
static class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isloggedin;

    // constructor
    public ClientHandler(Socket s, String name,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    @Override
    public void run() {

        String received;
        while (true)
        {
            try {
                String type = dis.readUTF();

                if (type.equals("1")) {

                    int fileNameLength = dis.readInt();

                    if (fileNameLength > 0) {
                        byte[] fileNameBytes = new byte[fileNameLength];
                        dis.readFully(fileNameBytes, 0, fileNameBytes.length);
                        String fileName = new String(fileNameBytes);

                        int fileContentLength = dis.readInt();

                        if (fileContentLength > 0) {
                            byte[] fileContentBytes = new byte[fileContentLength];
                            dis.readFully(fileContentBytes, 0, fileContentLength);
                            File fileToDownload = new File("C:\\Users\\Pelagia\\OneDrive - aueb.gr\\Desktop\\mediaFile\\marias_wedding.mp4");
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                                fileOutputStream.write(fileContentBytes);
                                fileOutputStream.close();
                            } catch (IOException error) {
                                error.printStackTrace();
                            }
                        }
                        for (ClientHandler mc : ar) {
                            // if the recipient is found, write on its
                            // output stream
                            if (mc.isloggedin == true) {
                                mc.dos.writeUTF(this.name + " : " + fileName);
                                break;
                            }
                        }
                    }


                } else if (type.equals("2")) {


                    // receive the string
                    received = dis.readUTF();

                    System.out.println(received);

                    if (received.equals("logout")) {
                        this.isloggedin = false;
                        this.s.close();
                        break;
                    }

                    // break the string into message and recipient part
                    StringTokenizer st = new StringTokenizer(received, "#");
                    String MsgToSend = st.nextToken();
                    String recipient = st.nextToken();

                    // search for the recipient in the connected devices list.
                    // ar is the vector storing client of active users
                    for (ClientHandler mc : ar) {
                        // if the recipient is found, write on its
                        // output stream
                        if (mc.name.equals(recipient) && mc.isloggedin == true) {
                            mc.dos.writeUTF(this.name + " : " + MsgToSend);
                            break;
                        }
                    }
                }
                } catch(IOException e){

                    e.printStackTrace();
                }

        }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
}
