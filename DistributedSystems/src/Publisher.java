import javafx.util.Pair;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

//User profile
public class Publisher implements Runnable {
    Socket client;
    ProfileName profileName;
    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;
    private byte[] buffer;
    private List<Topic> pubTopicList;
    private List<Broker> connectedBrokers;
    private String ipAddress = "127.0.0.1"; //must be taken dynamically so hash code can work correctly
    private int port = 1234; //must be taken dynamically so hush code can work correctly
    private HashMap<ProfileName, Pair<String, Value>> queueOfTopics;
    private static File currDirectory = new File(new File("").getAbsolutePath());



    /*
    public static void main(String[] args) throws SocketException {
        Publisher publisher = new Publisher(new ProfileName("Gigi"));
        publisher.push("Gigi", new Value());
    }
*/
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

    public List<Broker> getBrokerList(){
        return this.connectedBrokers;
    }

    void hashTopic(String topic) {
            Node nd = new Node();
            //Broker broker = new Broker();
            for (Broker brok : nd.brokers) {
                if (this.profileName.getUserVideoFilesMap().containsValue(topic)) ;
                {

                }
            }
    }

    void notifyBrokersNewMessage(String message) {

    }

    void notifyFailure(Broker fail) {

    }

    //synchronized method in order to avoid a race condition and
    // ALLOW only one thread to execute this block at any given time
    public synchronized void push(String topicName, Value multimediaFile) {
        try {
            client = new Socket("127.0.0.1", 4321);
            PublisherHandler handler = new PublisherHandler(client);
            Thread t = new Thread(handler);
            t.start();
        } catch (IOException e){
            e.printStackTrace();
        }
        /*
        try {
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            Pair<String, Value> pairObject = new Pair<>(topicName,multimediaFile);
            queueOfTopics.put(this.profileName, pairObject);
            out.writeUnshared(pairObject);
            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }*/

    /* open socket and create thread
         try {
            client = new Socket("127.0.0.1", 4321);
            PublisherHandler handler = new PublisherHandler(client);
            Thread t = new Thread(handler);
            t.start();
        } catch (IOException e){
            e.printStackTrace();
        } */

    }

    public void send(Socket client){
        String contents[] = currDirectory.list();
        for(String name : contents){
            System.out.println(name);
        }
        System.out.println("Write the name of the file you want to upload.");

        Scanner in = new Scanner(System.in);
        String fileName = in.nextLine();

        Value value = new Value(new MultimediaFile(fileName));

        push(fileName, value);

        while (true) {
            try {
                ObjectOutputStream stream = new ObjectOutputStream(client.getOutputStream());
                int port=client.getPort();
                String ip = client.getInetAddress().getHostAddress();
                for(Broker b: connectedBrokers) {
                    if (b.getIp().equals(ip) && b.getPort() == port) {
                        for (Topic t : b.getRelatedTopics()) {
                            //for (Queue<Value> q : queueOfTopics.get(t.getChannelName())) {
                               // for (Value v : q) {
                                    push(t.getChannelName(), new Value(new MultimediaFile(t.getChannelName())));
                              //  }
                           // }

                        }
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

/*
    private void setPubOwnTopics(String name) {
        Node n = new Node();
        ArrayList<String> tmp=n.readPublisherTopics(name);
        if(!getpubTopicList().isEmpty()){
            for(Topic t: getpubTopicList()){
                for(String s: tmp){
                    if(s.equals(t.getChannelName())){
                        this.getpubTopicList().add(t);
                    }
                }
            }
        }
    }

*/
    public List<Topic> getpubTopicList(){
        return this.pubTopicList;

    }

    //create func in broker to read topics and create pubTopicList from this
    public void setpubTopicList(Node n){
        this.pubTopicList= n.readTopicsList();
    }


    public static class PublisherHandler implements Runnable {
        private Socket client;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public PublisherHandler(Socket client) {
            this.client = client;

        }

        @Override
        public void run() {
            try {
                FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Pelagia\\OneDrive - aueb.gr\\Desktop\\ΚΑΤΑΝΕΜΗΜΕΝΑ\\Red_Kitten_01.jpg");
                File file = new File("C:\\Users\\Pelagia\\OneDrive - aueb.gr\\Desktop\\ΚΑΤΑΝΕΜΗΜΕΝΑ\\Red_Kitten_01.jpg");

                DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());
                String filename = "marias_wedding.mp4";
                byte[] fileNameBytes = filename.getBytes(); //StandardCharsets.UTF_8

                byte[] fileContentBytes = new byte[(int) file.length()];

                fileInputStream.read(fileContentBytes); //, 0, fileContentBytes.length);

                dataOutputStream.writeInt(fileNameBytes.length);
                dataOutputStream.write(fileNameBytes);

                dataOutputStream.writeInt(fileContentBytes.length);
                dataOutputStream.write(fileContentBytes);

                //dataOutputStream.flush();
                client.close();

            /*  Sending messages
                //out = new ObjectOutputStream(connection.getOutputStream());
                //in = new ObjectInputStream(connection.getInputStream());
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
               while (true)
                {
                    String message = inReader.readLine();
                    System.out.println(message);
                }*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}