import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import helpers.FileHelper;

public class Node {

    static List<Broker> brokers = new ArrayList<>();
    Broker broker = new Broker();
    static ServerSocket serverSocket;
    Socket client;
    int num;
    static String subject;
    static String ip;
    static int port;
    String brokerName;
    private static ArrayList<Topic> topicsList = new ArrayList<>();
    Publisher publisher = new Publisher();
    static Vector<ClientHandler> ar = new Vector<>();
    // private HashMap<String, String> topicsQueue;
    private HashMap<ClientHandler, ArrayList<String>> userMessQueue;

    private static File currDirectory = new File(new File("").getAbsolutePath());
    private static String topicsPath = currDirectory + "\\distributed_systems\\DistributedSystems\\data\\Topics.txt";
    private static String brokersPath = currDirectory
            + "\\distributed_systems\\DistributedSystems\\data\\Brokers.txt";

    /*
     * private static String topicsPath = currDirectory +
     * "\\distributed_systems\\DistributedSystems\\data\\Topics.txt";
     * private static String brokersPath = currDirectory +
     * "\\distributed_systems\\DistributedSystems\\data\\Brokers.txt";
     * private static String publishersPath = currDirectory +
     * "\\distributed_systems\\DistributedSystems\\data\\Publishers.txt";
     * 
     */

    public static ArrayList<Topic> readTopicsList() {
        // ArrayList<Topic> topics = new ArrayList<>();
        ArrayList<String> topicsLines = FileHelper.readFile(topicsPath);
        for (String line : topicsLines) {
            topicsList.add(new Topic(line));
        }
        return topicsList;
    }

    public static List<Broker> loadBrokers() {
        ArrayList<String> brokersLines = FileHelper.readFile(brokersPath);
        for (String line : brokersLines) {
            String[] data = line.split(" , ");
            brokers.add(new Broker(data[0], data[1], Integer.parseInt(data[2])));
        }
        return brokers;
    }

    private static void hashOfBrokers() throws NoSuchAlgorithmException {

        for (Broker br : brokers) {
            // int ipPlusPort = Integer.parseInt(br.getIp()) + br.getPort();
            String ipPlusPort = br.getIp() + br.getPort();
            // String strIpPlusPort = Integer.toString(ipPlusPort);
            br.setbHashValue(hashCode(ipPlusPort));
        }
    }

    private static Long hashCode(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        return no.longValue();
    }

    public static void calculateKeys() throws NoSuchAlgorithmException {
        loadBrokers();
        readTopicsList();
        hashOfBrokers();
        HashMap<String, Long> topicHashes = calculateTopicHash();

        for (Topic c : topicsList) {
            boolean flag = false;
            for (Broker b : brokers) {
                if (c.hashCode() >= b.hashCode()) {
                    continue;
                }
                b.linkedTopics.add(c);
                flag = true;
            }
            if (!flag) {
                brokers.get(0).linkedTopics.add(c);
            }
        }

    }

    // returns list of brokers' hash
    private static ArrayList<Long> allBrokerHash() {
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

    private static HashMap<String, Long> calculateTopicHash() throws NoSuchAlgorithmException {

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

    public ArrayList<Topic> gettopicsList() {
        return this.topicsList;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        calculateKeys();
        // Vector to store active clients

        // counter for clients
        int i = 0;
        // server is listening on port 1234
        serverSocket = new ServerSocket(1234);

        Socket s;

        // running infinite loop for getting
        // client request
        while (true) {
            // Accept the incoming request
            s = serverSocket.accept();

            System.out.println("New client request received : " + s);

            // obtain input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            String username = dis.readUTF();
            subject = dis.readUTF();
            System.out.println("connected to subject: " + subject);

            System.out.println("Creating a new handler for this client...");

            // Create a new handler object for handling this request.
            ClientHandler mtch = new ClientHandler(s, username, subject, dis, dos);

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
    static class ClientHandler implements Runnable {
        Scanner scn = new Scanner(System.in);
        private String name;
        final DataInputStream dis;
        final DataOutputStream dos;
        Socket s;
        boolean isloggedin;
        Broker broker;
        String subject;
        Topic topic;

        // constructor
        public ClientHandler(Socket s, String name, String subject,
                DataInputStream dis, DataOutputStream dos) {
            this.dis = dis;
            this.dos = dos;
            this.name = name;
            this.subject = subject;
            this.topic = new Topic(subject);
            this.s = s;
            this.isloggedin = true;
            boolean brokFound = false;
            for (Broker br : brokers) {
                if (brokFound)
                    break;
                for (Topic c : br.linkedTopics) {

                    if (c.getChannelName().equals(subject)) {
                        broker = br;
                        brokFound = true;
                        break;
                    }
                }
            }

        }

        @Override
        public void run() {

            String received;
            while (true) {
                try {
                    String type = dis.readUTF();
                    Queue<String> topicsMessages = broker.topicsQueue.get(this.topic);
                    for (String tM : topicsMessages) {
                        this.dos.writeUTF(tM);
                    }
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
                                File fileToDownload = new File(
                                        "C:\\Users\\Cosmic Travellers\\Desktop\\downloads\\logo_new.jpg");
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
                                    if (broker.topicsQueue.get(this.topic) == null) {
                                        broker.topicsQueue.put(this.topic, new LinkedList<String>());
                                    }
                                    broker.topicsQueue.get(this.topic)
                                            .add(this.name + " : File Upload Successful");
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
                                if (broker.topicsQueue.get(this.topic) == null) {
                                    broker.topicsQueue.put(this.topic, new LinkedList<String>());
                                }
                                broker.topicsQueue.get(this.topic).add(this.name + " : " + MsgToSend);
                                mc.dos.writeUTF(this.name + " : " + MsgToSend);
                                break;
                            }
                        }
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
            try {
                // closing resources
                this.dis.close();
                this.dos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
