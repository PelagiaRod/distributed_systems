
// import javafx.util.Pair;
import helpers.FileHelper;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

//User profile
public class Publisher implements Runnable {
    //Socket client;
    static String username;
    ProfileName profileName;
    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;
    private byte[] buffer;
    private List<Topic> pubTopicList;
    private List<Broker> connectedBrokers;
    //private String ipAddress = "127.0.0.1"; // must be taken dynamically so hash code can work correctly
    //private int port = 1234; // must be taken dynamically so hush code can work correctly
    private HashMap<ProfileName, AbstractMap.SimpleEntry<String, Value>> queueOfTopics;
    static Socket client;

    private static File currDirectory = new File(new File("").getAbsolutePath());
    private static String topicsPath = currDirectory + "\\distributed_systems\\DistributedSystems\\data\\Topics.txt";


     public static void main(String[] args) throws IOException {

         System.out.println("Please enter your name:");
         Scanner scanner = new Scanner(System.in);
         username = scanner.nextLine();
         Publisher publisher = new Publisher(new ProfileName(username));

         System.out.println("Select a topic");
         Scanner myTopic = new Scanner(System.in);
         String subject = myTopic.nextLine();
         ArrayList<Topic> topics = Node.readTopicsList();

         for (Topic topic : topics) {
             if (topic.getChannelName().equals(subject)) {

                 System.out.println("1. Upload file. \n 2. Write text.");
                 String type = scanner.nextLine();
                 client = new Socket("127.0.0.1", 1234);
                 publisher.push(subject, type);
                 break;
             }
         }


      }


    public Publisher() {

    }

    public Publisher(ProfileName profileName) {
        this.profileName = profileName;
    }

    @Override
    public void run() {
    }

    ArrayList<Value> generateChunks(MultimediaFile file) {
        Value value = new Value(file);
        ArrayList<Value> chunks = new ArrayList<>();
        return chunks;
    }

    public List<Broker> getBrokerList() {
        return this.connectedBrokers;
    }

    // TODO
    void hashTopic(String topic) {
        Node nd = new Node();
        // Broker broker = new Broker();
        for (Broker brok : nd.brokers) {
            if (this.profileName.getUserVideoFilesMap().containsValue(topic))

            {

            }
        }
    }

    void notifyBrokersNewMessage(String message) {

    }

    void notifyFailure(Broker fail) {

    }

    // synchronized method in order to avoid a race condition and
    // ALLOW only one thread to execute this block at any given time
    public synchronized void push(String subject, String type) {
        try {
            //Socket client = new Socket("127.0.0.1", 1234);
            DataInputStream dis = new DataInputStream(client.getInputStream());
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());

            //Scanner scn = new Scanner(System.in);
            // read the message to deliver.
            //String msg; // = scn.nextLine();
            if (type.equals("1") || type.equals("2")) {
                // sendMessage thread
                Thread sendMessage = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        switch (type) {
                            case "1":
                                try {
                                    dos.writeUTF("1");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                upload(client);
                                System.out.println("Write 1 or 2");
                                Scanner scn = new Scanner(System.in);
                                String type = scn.nextLine();
                                push(subject, type);
                                break;
                            case "2":
                                // while (true) {
                                scn = new Scanner(System.in);
                                // read the message to deliver.
                                String msg = scn.nextLine();

                                try {
                                    // write on the output stream
                                    dos.writeUTF("2");
                                    dos.writeUTF(username + "#" + msg);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                // }
                                System.out.println("Write 1 or 2");
                                type = scn.nextLine();
                                push(subject, type);
                                break;

                            default:
                                System.out.println("You must select either 1 or 2");
                                break;
                        }
                    }


                });

                sendMessage.start();
            }
            } catch(IOException e){
                e.printStackTrace();
            }


    }

    public List<Topic> getpubTopicList() {
        return this.pubTopicList;

    }

    // create func in broker to read topics and create pubTopicList from this
    public void setpubTopicList(Node n) {
        this.pubTopicList = n.readTopicsList();
    }


        public void upload(Socket client) {
            try {
                /*
                String contents[] = currDirectory.list();
                for (String name : contents) {
                    System.out.println(name);
                }
                System.out.println("Write the name of the file you want to upload.");

                Scanner in = new Scanner(System.in);
                String fileName = in.nextLine();

                Value value = new Value(new MultimediaFile(fileName));
                */
                FileInputStream fileInputStream = new FileInputStream(
                        "C:\\Users\\Pelagia\\OneDrive - aueb.gr\\Desktop\\ΚΑΤΑΝΕΜΗΜΕΝΑ\\Red_Kitten_01.jpg");
                File file = new File(
                        "C:\\Users\\Pelagia\\OneDrive - aueb.gr\\Desktop\\ΚΑΤΑΝΕΜΗΜΕΝΑ\\Red_Kitten_01.jpg");

                DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                String filename = "Red_Kitten_01.jpg";
                byte[] fileNameBytes = filename.getBytes(); // StandardCharsets.UTF_8

                byte[] fileContentBytes = new byte[(int) file.length()];

                fileInputStream.read(fileContentBytes); // , 0, fileContentBytes.length);

                dataOutputStream.writeInt(fileNameBytes.length);
                dataOutputStream.write(fileNameBytes);

                dataOutputStream.writeInt(fileContentBytes.length);
                dataOutputStream.write(fileContentBytes);

                dataOutputStream.flush();

                /*
                 * Sending messages
                 * //out = new ObjectOutputStream(connection.getOutputStream());
                 * //in = new ObjectInputStream(connection.getInputStream());
                 * BufferedReader inReader = new BufferedReader(new
                 * InputStreamReader(System.in));
                 * while (true)
                 * {
                 * String message = inReader.readLine();
                 * System.out.println(message);
                 * }
                 */
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


}
