
// import javafx.util.Pair;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import models.MultimediaFile;
import models.ProfileName;
import models.Topic;
import models.Value;

//User profile
public class Publisher implements Runnable {
    // Socket client;
    static String username;
    ProfileName profileName;
    private List<Broker> connectedBrokers;

    private HashMap<ProfileName, AbstractMap.SimpleEntry<String, Value>> queueOfTopics;
    static Socket client;

    private static File currDirectory = new File(new File("").getAbsolutePath());

    public static void main(String[] args) throws IOException {

        System.out.println("Please enter your name:");
        Scanner scanner = new Scanner(System.in);
        username = scanner.nextLine();
        Publisher publisher = new Publisher(new ProfileName(username));

        System.out.println("Select a topic");
        Scanner myTopic = new Scanner(System.in);
        String subject = myTopic.nextLine();
        ArrayList<Topic> topics = Node.loadTopics();

        for (Topic topic : topics) {
            if (topic.getChannelName().equals(subject)) {

                System.out.println("1. Upload file. \n2. Write text.");
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
            // Socket client = new Socket("127.0.0.1", 1234);
            DataInputStream dis = new DataInputStream(client.getInputStream());
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            dos.writeUTF(username);
            dos.writeUTF(subject);
            // Scanner scn = new Scanner(System.in);
            // read the message to deliver.
            // String msg; // = scn.nextLine();
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void upload(Socket client) {
        try {
            /*
             * String contents[] = currDirectory.list();
             * for (String name : contents) {
             * System.out.println(name);
             * }
             * System.out.println("Write the name of the file you want to upload.");
             * 
             * Scanner in = new Scanner(System.in);
             * String fileName = in.nextLine();
             * 
             * Value value = new Value(new MultimediaFile(fileName));
             */
            FileInputStream fileInputStream = new FileInputStream(currDirectory + "\\data\\monilinia.jpg");
            File file = new File(currDirectory + "\\data\\monilinia.jpg");

            DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
            String filename = "monilinia.jpg";
            byte[] fileNameBytes = filename.getBytes(); // StandardCharsets.UTF_8

            byte[] fileContentBytes = new byte[(int) file.length()];

            fileInputStream.read(fileContentBytes); // , 0, fileContentBytes.length);

            dataOutputStream.writeInt(fileNameBytes.length);
            dataOutputStream.write(fileNameBytes);

            dataOutputStream.writeInt(fileContentBytes.length);
            dataOutputStream.write(fileContentBytes);

            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
