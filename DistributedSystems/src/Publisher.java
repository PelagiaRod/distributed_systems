import javafx.util.Pair;
import java.io.*;
import java.net.*;
import java.util.*;

//User profile
public class Publisher implements Runnable {
    Socket client;
    ProfileName profileName;
    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;
    private byte[] buffer;
    private List<Topic> pubTopicList;
    private String ipAddress = "127.0.0.1"; //must be taken dynamically so hash code can work correctly
    private int port = 1234; //must be taken dynamically so hush code can work correctly
    private HashMap<ProfileName, Pair<String, Value>> queueOfTopics;



    public static void main(String[] args) throws SocketException {
        Publisher publisher = new Publisher(new ProfileName("Gigi"));
        publisher.push("Gigi", new Value());
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
        ArrayList<Value> chunks = new ArrayList<>();
        return chunks;
    }

    void getBrokerList() {
    }

    Broker hashTopic(String topic) {
        Node nd = new Node();
        for(Broker broker : nd.brokers){
            if (this.profileName.getUserVideoFilesMap().containsValue(topic));
            {
                return broker;
            }
        }
        Broker broker = new Broker();
        //key = IP+port % number of users?
        //TODO: finish hash code
        return broker;
    }

    void notifyBrokersNewMessage(String message) {

    }

    void notifyFailure(Broker fail) {

    }

    void push(String topicName, Value multimediaFile) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            Pair<String, Value> pairObject = new Pair<>(topicName,multimediaFile);
            queueOfTopics.put(this.profileName, pairObject);
            out.writeUnshared(pairObject);
            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }




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
        while (true) {
            try {
                ObjectOutputStream stream = new ObjectOutputStream(client.getOutputStream());
                int port=client.getPort();
                String ip = client.getInetAddress().getHostAddress();
                for(Broker b: connectedBrokers) {
                    if(b.getIp().equals(ip)&&b.getPort()==port) {
                        for( Topic t : b.getRelatedTopics()) {
                            for(Queue<Value> q: queueOfTopics.get(t.getChannelName())){
                                for(Value v : q){
                                    push(t,v);
                                }
                            }

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


    public List<Topic> getpubTopicList(){
        return this.pubTopicList;

    }

    //create func in broker to read topics and create pubTopicList from this
    public void setpubTopicList(Node n){
        this.pubTopicList= n.getTopicsList();
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
/* TRASH CODE
might be useless
        try{
            //working TCP code
            Socket client = new Socket("127.0.0.1", 4321);

            String str = "Paul";

            OutputStreamWriter os = new OutputStreamWriter(client.getOutputStream());
            PrintWriter out = new PrintWriter(os);
            out.println(str);
            os.flush();

            PublisherHandler handler = new PublisherHandler(client);
            Thread t = new Thread(handler);
            t.start();

            //UDP code
           DatagramSocket ds = new DatagramSocket();
           MultimediaFile mf = new MultimediaFile();
           byte[] b = mf.getMultimediaFileChunk();
           InetAddress ia = InetAddress.getLocalHost();
           DatagramPacket dp = new DatagramPacket(b, b.length, ia, 4321);
           ds.send(dp);

           byte[] b1 = new byte[1024];
           DatagramPacket dp1 = new DatagramPacket(b, b.length);
           ds.receive(dp1);

           String str = new String(dp1.getData());
           System.out.println("result i: " + str);

           //for Server
            DatagramSocket datagramSocket = new DatagramSocket(4321);

            byte[] bt = new byte[1024];

            DatagramPacket dp2 = new DatagramPacket(bt, bt.length);
        } catch (IOException e){
            e.printStackTrace();
        }

                /*
                String messageToSend = scanner.nextLine();
                buffer = messageToSend.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 1234);
                datagramSocket1.send(datagramPacket);
                DatagramSocket datagramSocket1 = new DatagramSocket();
                byte[] buffer1;

                From PublisherHandler run()
               try{
                DatagramSocket datagramSocket1 = new DatagramSocket();
                byte[] buffer1;
                Scanner scanner = new Scanner(System.in);
                String messageToSend = scanner.nextLine();
                buffer1 = messageToSend.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buffer1, buffer1.length, InetAddress.getLocalHost(), 1234);
                datagramSocket1.send(datagramPacket);
                //datagramSocket1.receive(datagramPacket);
                //String messageFromServer = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                //System.out.println("The server says : " + messageFromServer);
            } catch(IOException e){
                e.printStackTrace();
            }

 */
/* send text
        try {
            Socket clientSocket = new Socket("127.0.0.1", 4321);
            System.out.println("Hi there!");

            while (true) {
                System.out.println("Enter your text: ");
                Scanner sc = new Scanner(System.in);
                String str = sc.nextLine();
                OutputStreamWriter os = new OutputStreamWriter(clientSocket.getOutputStream());
                PrintWriter out = new PrintWriter(os);
                out.println(str);
                os.flush();
                PublisherHandler handler = new PublisherHandler(clientSocket);
                Thread t = new Thread(handler);
                t.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
            // break;
        }*/