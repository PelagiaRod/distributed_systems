import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import models.Topic;

public class Broker extends Node {

    ServerSocket serverSocket;
    String name, ip;
    int port;
    Socket client;
    int num;
    String subject;
    String brokerName;
    Vector<ClientHandler> clients;
    public HashMap<Topic, Queue<String>> topicsQueue = new HashMap<>();
    List<Topic> linkedTopics = new ArrayList<Topic>(); // hashmap == queue
    private static File currDirectory = new File(new File("").getAbsolutePath());

    public Broker(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        clients = new Vector<>();
    }

    // TODO NEED REFACTOR TO ADD LINKEDTOPICS TO THIS BROKER
    public void calculateKeys() throws NoSuchAlgorithmException {
        loadTopics();
        loadBrokers();
        // Add linkedTopics itterating to brokersList
        for (Topic c : topics) {
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

        // Check which broker of brokersList is this object and add to this linkedTopics
        for (Broker broker : brokers) {
            if (broker.equals(broker))
                this.linkedTopics = broker.linkedTopics;
        }

    }

    public void start() throws NoSuchAlgorithmException, IOException {
        calculateKeys();
        // server is listening on port 1234
        serverSocket = new ServerSocket(port);
        System.out.println(name + " start and listening on port " + port);
        // running infinite loop for getting
        // client request
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New client request received : " + socket);
                // obtain input and output streams
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                // Listen for input from connection
                // Check for client disconnection

                String username = dis.readUTF();

                subject = dis.readUTF();
                System.out.println("connected to subject: " + subject);

                // Create a new handler object for handling this request.
                ClientHandler mtch = new ClientHandler(socket, username, subject, dis, dos);
                if (!mtch.broker.equals(this)) {
                    dis.close();
                    dos.close();
                    socket.close();
                }
                // Create a new Thread with this object.
                Thread t = new Thread(mtch);

                // add this client to active clients list
                clients.add(mtch);

                // start the thread.
                t.start();
                return;
            } catch (IOException e) {
                throw e;
            }
        }
    }

    // ClientHandler class
    class ClientHandler implements Runnable {
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
                    Queue<String> topicsMessages = broker.topicsQueue.get(this.topic);
                    if (topicsMessages != null) {
                        for (String tM : topicsMessages) {
                            this.dos.writeUTF(tM);
                        }
                    }
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
                                File directory = new File(currDirectory + "\\data\\downloads");
                                if (!directory.exists())
                                    directory.mkdir();

                                File fileToDownload = new File(currDirectory + "\\data\\downloads\\" + fileName);
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                                    fileOutputStream.write(fileContentBytes);
                                    fileOutputStream.close();
                                } catch (IOException error) {
                                    error.printStackTrace();
                                }
                            }
                            for (ClientHandler mc : clients) {
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
                        String recipient = st.nextToken();
                        String MsgToSend = st.nextToken();

                        // search for the recipient in the connected devices list.
                        // clients is the vector storing client of active users
                        for (ClientHandler mc : clients) {
                            // if the recipient is found, write on its
                            // output stream
                            if (mc.isloggedin == true) {
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

    // Overriding equals() to compare two Complex objects
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /*
         * Check if o is an instance of Complex or not
         * "null instanceof [type]" also returns false
         */
        if (!(o instanceof Broker)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Broker b = (Broker) o;

        return port == b.port && ip.equals(b.ip) && name.equals(b.name);
    }
}
