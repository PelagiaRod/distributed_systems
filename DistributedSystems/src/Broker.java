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



public class Broker extends Thread {
    //push(topic,value) -> [broker]
    //pull(topic,[broker]) -> [topic,value]

    List<Consumer> registeredUsers;
    List<Publisher> registeredPublishers;
    private DatagramSocket datagramSocket;
    private byte[] buffer = new byte[256];
    String topic;

    //brokers list must be static because they are the same, no matter the instance
    private static List<Broker> allBrokers;
    private String name, ip;
    private int port;
    private long hashBroker; // == hashBroker
    private List<Consumer> registeredConsumers;
    private List<Topic> relatedTopics;		//hashmap == queue
    private HashMap<Topic,ArrayList<Queue<Value>>> topicsQueue;
    private static List<Consumer> allConsumers;
    private static List<Publisher> allPublishers;
    private ServerSocket serverSocket;
    private HashMap<Topic,ArrayList<Queue<Value>>> queueOfTopics;	//==topicsQueue




    /* moved in Node
    private static File currDirectory = new File(new File(".").getAbsolutePath());
    private static String currDirectoryPath = currDirectory.getAbsolutePath().substring(0,currDirectory.getAbsolutePath().length() - 1);
    private static String homePath = currDirectory + "Database\\";
    private static String brokers = homePath + "Brokers.txt";
    private static String topicsPath = homePath + "Topics.txt";
    private  ArrayList<Topic> topicsList= new ArrayList<>();
    private  ArrayList<Broker> brokersList= new ArrayList<>();
    */

    public static void main(String args[]) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4321);
        Broker server = new Broker(serverSocket);
        server.pull("Hello");
    }

    public Broker(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    public Broker(){
    }

    public Broker(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.registeredPublishers = new ArrayList<>();
        this.registeredConsumers = new ArrayList<>();
        this.relatedTopics = new ArrayList<>();
        this.topicsQueue = new HashMap<>();
        this.allPublishers = new ArrayList<>();
        this.allConsumers = new ArrayList<>();
        init();
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    public void setQueueOfTopics(HashMap<Topic,ArrayList<Queue<Value>>> q){
        this.queueOfTopics = q;
    }
    public HashMap<Topic,ArrayList<Queue<Value>>> getQueueOfTopics(){
        return this.queueOfTopics;
    }


    public void init() {
        Node n = new Node();
        // n.readRouteCodes();
        this.allBrokers=n.loadBrokers();
        //dhmiourgei lista me ta topics kai arxikopoiei ta related topics
        n.readtopicsList();
        this.relatedTopics = n.getTopicsList();
        //setPubOwnTopics(this.name);
        setTopicQueue(n);
        connectToBroker();
    }

    public List<Topic> getRelatedTopics(){
        return this.relatedTopics;
    }

    void setTopicQueue(Node n){
        //TODO: hashmap
    }

    private void connectToBroker() {
        //TODO
    }

    public Broker(String topic){
        this.topic = topic;
    }

    public Broker(DatagramSocket datagramSocket)    {
        this.datagramSocket = datagramSocket;
    }

    public Broker(List<Consumer> registeredUsers, List<Publisher> registeredPublishers){
        this.registeredUsers = registeredUsers;
        this.registeredPublishers = registeredPublishers;
    }



    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    Consumer acceptConnection(Consumer name) {
        for (Consumer registeredUser : registeredUsers){
            if (registeredUser == name){
                //connect
            }
        }
        registeredUsers.add(name);
        return name;
    }

    Publisher acceptConnection(Publisher publisher){
        for (Publisher registeredPublisher : registeredPublishers){
            if (registeredPublisher == publisher){
                //connect
            }
        }
        registeredPublishers.add(publisher);
        return publisher;
    }
    void filterConsumers(String consumers){
    }
    void notifyBrokersOnChanges(){
    }
    void notifyPublishers(String publisher){
        for (Publisher registeredPublishers : registeredPublishers)
        {
        }
    }

    //The input to the hash function is of arbitrary length but output is always of fixed length.
    public void calculateKeys() throws NoSuchAlgorithmException {
        //calculate each of the Brokers hash value
        Publisher p = new Publisher();
        hashOfBrokers();
        HashMap<String, Long> topicHashes = calculateTopicHash();
        List<Topic> copyTopics = new ArrayList<>();
        for (Topic t: p.getpubTopicList()){
            copyTopics.add(t);
        }
        //compare topic hashes and broker hash value
        if (!topicHashes.isEmpty()) {
            ArrayList<Long> allBrokHash = allBrokerHash();
            if (allBrokHash!=null) {
                //iterate the list of the values of the brokers' hash
                for (int i=0; i<allBrokHash.size(); i++) {
                    // gia kathe apo ta topic pou yparxoun sth lista me ta topics tou publisher
                    for (Topic t : p.getpubTopicList()) {
                        if(copyTopics.contains(t)){
                            // pare apo thn topicHashes lista, ayto to object pou antistoixei sto ekastote topic
                            long h = topicHashes.get(t.getChannelName());
                            // take the number of elements of the list minus 1 (-1)
                            int s = allBrokHash.size() - 1;

                            if (i==0) { //first iteration
                                //if h is smaller than broker1 or bigger than the last broker
                                if ( (h < allBrokHash.get(i)) || (h >= allBrokHash.get(s)) ){
                                    for (Broker b: getAllBrokers()) {
                                        if (b.getHash() == allBrokHash.get(i)){
                                            b.relatedTopics.add(t);
                                            int index=copyTopics.indexOf(t);
                                            //remove from copyTopics list the one that have just been added to broker
                                            copyTopics.remove(index);
                                        }
                                    }
                                }
                            }
                            //i.e: if h is smaller than broker3 and bigger than broker2, put it in broker3 (i)
                            else if ( h < allBrokHash.get(i) && h >= allBrokHash.get(i-1)){
                                for (Broker b: getAllBrokers()) {
                                    if (b.getHash() == allBrokHash.get(i)){
                                        b.relatedTopics.add(t);
                                        int index=copyTopics.indexOf(t);
                                        copyTopics.remove(index);
                                    }
                                }
                            }
                            else
                                continue;
                        }

                    }
                }
            }
        }
        for(Broker b: getAllBrokers()){
            //System.out.println(b.getName()+" "+b.getHash());
            HashMap<Topic,ArrayList<Queue<Value>>> qPair = new HashMap<>();
            // gia kathe broker, pare ola ta related topic tou kai valta se mia queue
            //etsi wste h qPair na exei ta topics tou kathe broker
            for(Topic t : b.getRelatedTopics()){
                ArrayList<Queue<Value>> val = new ArrayList<>();;
                qPair.put(t,val);
            }
            b.setTopicQueue(qPair);
        }
    }


    //returns sorted list of brokers' hash value
    private ArrayList<Long> allBrokerHash() {
        ArrayList<Long> allBrokHash = new ArrayList<>();
        if(!getAllBrokers().isEmpty()) {
            for (Broker b : getAllBrokers()) {
                allBrokHash.add(b.getHash());
            }   //sort the hash to be in order
            Collections.sort(allBrokHash);
            return allBrokHash;
        }
        else{
            return null;
        }
    }


    // hashCode calculates the hash value of the string input (of the topic name)
    private Long hashCode(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        /* 1. Dhmiourgoume ena object typou MessageDigest me ton typo tou algorithmou
            ths hash synarthshs pou theloume na ylopoihsoume
            2. meta mesw aytou tou antikeimenou( md) vazoume se ena byte array
                thn hashed timh tou input pou exoume dwsei
            3. telow metatrepoume to byte array se arithmo typou BigInteger
            4. Telos kanoume thn telikh metatropi thw hash timhs, se long typo.
        * */
        byte[] messageDigest = md.digest(input.getBytes());
        // Convert byte array into signum representation
        BigInteger no = new BigInteger(1, messageDigest);
        //convert BigInteger to long and return it
        return no.longValue();
    }


    //get all the topics and calculate a hash value for each topic and put them inside topicHashes list and return
    private HashMap<String, Long> calculateTopicHash() throws NoSuchAlgorithmException {
        List<Topic> topics = getRelatedTopics();
        HashMap<String, Long> topicHashes = new HashMap<>();
        for (Topic t : topics) {
            long h = hashCode(t.getChannelName());
            //put inside topicHashes queue the name and corresponding hash value of each topic
            topicHashes.put(t.getChannelName(), h);
        }
        return topicHashes;
    }

    private void hashOfBrokers() throws NoSuchAlgorithmException {
        //brokers is a list of all available brokers
        List<Broker> brokers = getAllBrokers();
        //iterate the list of brokers
        for (Broker br : brokers) {
            //String input= this.getIp()+" "+Integer.toString(this.getPort());
            //foreach broker found in the list, split its ip string value and return it inside "parts" array
            //kathe thesi tou pinaka xwrizetai apo ta kommatia pou kathorizontai apo to regex (escape ".")
            // this split splits ip number and creates the string array: {192, 168, 10, 23}
            String[] parts = br.getIp().split("\\.");

           // ITS WORKING PROPERLY (merges the parts string array into one string)
            // StringBuilder is a method that allows appending string representation of char array
            //StringBuilder sb = new StringBuilder("");
            String result = "";
            for (int j = 0; j < parts.length; j++) {
                result = result + parts[j];
            }   // result = 1921681025 (string)
            int ipPlusPort = Integer.parseInt(result) + br.getPort();
            String strIpPlusPort = Integer.toString(ipPlusPort);
            //an h ip tou broker pou ektelei thn parousa synarthsh einai h idia me thn ip apo thn synoliki lista twn
            //brokers, epishs an tautoxrona to port tou parontos broker einai to idio me ayto pou eksetazetai, tote vale
            //ws hash timh aytou touu broker to apotelesma tou hash pou tha vgalei h synarthsh me to sygkekrimeno input
            if ((this.getIp().equals(br.getIp())) && (this.getPort() == br.getPort())) {
                this.hashBroker = hashCode(strIpPlusPort);
            } else {
                //alliws vale ston broker pou exoume ayth th stigmh sto iteration, to apotelesma tou hash code tou input
                br.setHash(hashCode(strIpPlusPort));
            }
        }
    }

    /*
            void pull(String brokerName) {
            try {
            while(true) {
            Socket client = serverSocket.accept();
            System.out.println("Publisher is connected!");
            //serverSocket = new ServerSocket(4321);
            BrokerHandler handler = new BrokerHandler(client);
            //handler.run();
            Thread thread = new Thread(handler);
            thread.start();
            }
            } catch (IOException e){
            closeBroker();
            //e.printStackTrace();
            }
            }
    public void closeBroker(){
            try{
            if(serverSocket != null)
            {
            serverSocket.close();
            }
            }catch (IOException e){
            e.printStackTrace();
            }
            }
    */

    public List<Broker> getAllBrokers(){
        return this.allBrokers;
    }
    public void setTopicQueue(HashMap<Topic,ArrayList<Queue<Value>>> tq){
        this.topicsQueue = tq;
    }
    public void setHash(long longInput){
        this.hashBroker = longInput;
    }
    public Long getHash() {
        return this.hashBroker;
    }



    //den xreiazetai setter, ta pairnei dynamika otan diavazoume ta topics apo to arxeio

    /*
        public void setTopicQueue(HashMap<Topic,ArrayList<Queue<Value>>> tq){
            this.topicsQueue = tq;
        }
        public HashMap<Topic,ArrayList<Queue<Value>>> getQueueOfTopics(){
            return this.topicsQueue;
        }
        public List<Broker> getAllBrokers(){
            return this.topicsQueue;
        }
    */
    void pull(String brokerName) {

        try {
            while(true) {
                Socket client = serverSocket.accept();
                System.out.println("Publisher is connected!");
                //serverSocket = new ServerSocket(4321);
                BrokerHandler handler = new BrokerHandler(client);
                //handler.run();
                Thread thread = new Thread(handler);
                thread.start();
            }
        } catch (IOException e){
            closeBroker();
            //e.printStackTrace();
        }

    }

    public void closeBroker(){
        try{
            if(serverSocket != null)
            {
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }




    static class BrokerHandler implements Runnable {
        private Socket client;
        //private ObjectOutputStream out;
        //private ObjectInputStream in;
        private PrintWriter out;
        private BufferedReader in;
        byte[] fileData;

        public BrokerHandler(Socket client) {
            this.client = client;

        }

        @Override
        public void run() {
            //while (client.isConnected()) {
            try {
                DataInputStream dataInputStream = new DataInputStream(client.getInputStream());

                int fileNameLength = dataInputStream.readInt();

                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName = new String(fileNameBytes);

                    int fileContentLength = dataInputStream.readInt();

                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
                        File fileToDownload = new File("C:\\Users\\Pelagia\\OneDrive - aueb.gr\\Desktop\\mediaFile\\Red_Kitten_01.jpg");
                        try{
                            FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                            fileOutputStream.write(fileContentBytes);
                            fileOutputStream.close();
                        }catch(IOException error){
                            error.printStackTrace();
                        }
                    }


                    System.out.println(fileName);
                }

                /*
                   SEND MESSAGES
                    BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String str = br.readLine();
                    while (str != null) {
                        System.out.println("Publisher data : " + str);
                        str = br.readLine();
                    }*/
            } catch (IOException e) {
                e.printStackTrace();
                //break;
            }
            //  }
        }
    }
}

/* TRASH CODE THAT MIGHT BE USEFUL
        //TCP
        try {
            while(true) {
                ServerSocket server = new ServerSocket(1234);
                Socket client = server.accept();
                System.out.println("Consumer is connected!");
                BrokerHandler handler = new BrokerHandler(client);
                //handler.run();
                Thread thread = new Thread(handler);
                thread.start();
            }
            //registeredUsers.add(name);
            //BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //String str = br.readLine();
            //System.out.println("Client data : " + str);
        } catch (IOException e){
            e.printStackTrace();
        }
*/
        /*
        ServerSocket server = new ServerSocket(1234);
        Socket client = server.accept();
        //BrokerHandler handler = new BrokerHandler(client);
        //handler.run();
        Node nd = new Node();
        nd.init(1234);
        */

/*try {
                //in = new ObjectInputStream(client.getInputStream());
                //out = new ObjectOutputStream(client.getOutputStream());
                //out.flush();
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String str = br.readLine();
                System.out.println("Publisher data : " + str);
            } catch (IOException e) {
                    e.printStackTrace();
                }
        /*
        //TCP
        try {
            while(true) {
                ServerSocket server = new ServerSocket(4321);
                Socket client = server.accept();
                System.out.println("Publisher is connected!");
                BrokerHandler handler = new BrokerHandler(client);
                handler.run();
                Thread thread = new Thread(handler);
                thread.start();
            }
            //BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //String str = br.readLine();
            //System.out.println("Publisher data : " + str);
        } catch (IOException e){
            e.printStackTrace();
        }
*/
        /*
        try {
            providerSocket = new ServerSocket(1234);
            while (true) {
                Socket connection = providerSocket.accept();
                Thread thread = new Thread((Runnable) connection);
                thread.start();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
         */
