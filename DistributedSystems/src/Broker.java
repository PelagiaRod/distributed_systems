import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Broker extends Thread {
    //push(topic,value) -> [broker]
    //pull(topic,[broker]) -> [topic,value]

    List<Consumer> registeredUsers;
    List<Publisher> registeredPublishers;
    private DatagramSocket datagramSocket;
    private byte[] buffer = new byte[256];
    String topic;

    public static void main(String args[]) throws IOException {
        DatagramSocket datagramSocket = new DatagramSocket(1234);
        Broker server = new Broker(datagramSocket);
        server.pull("Hello");
    }

    public Broker(){

    }

    public Broker(String topic){
        this.topic = topic;
    }

    public Broker(DatagramSocket datagramSocket)
    {
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

    void calculateKeys(){}

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
    public void calculateKeys() {
        //calculate each of the Brokers hash value
        hashOfBrokers();
        //calculate TopicsHash
        HashMap<String, Long> topicHashes = calculateTopicHash();
        List<Topic> copyTopics = new ArrayList<>();
        for(Topic t:getTopics()){
            copyTopics.add(t);
        }
        //compare topic hashes and broker
        if (!topicHashes.isEmpty()) {
            ArrayList<Long> allBrokHash = allBrokerHash();
            if(allBrokHash!=null) {
                for (int i=0;i<allBrokHash.size();i++) {
                    for (Topic t : getTopics()) {
                        if(copyTopics.indexOf(t)>-1){
                            long h = topicHashes.get(t.getBusLine());
                            int s = allBrokHash.size()-1;


                            if(i==0){
                                if((h<allBrokHash.get(i))||(h>=allBrokHash.get(s))){
                                    for(Broker b: getBrokers()) {
                                        if(b.getHash()==allBrokHash.get(i)){
                                            b.relatedTopics.add(t);
                                            int index=copyTopics.indexOf(t);
                                            copyTopics.remove(index);
                                        }
                                    }
                                }
                            }

                            else if (h<allBrokHash.get(i)&&h>=allBrokHash.get(i-1)){
                                for(Broker b: getBrokers()) {
                                    if(b.getHash()==allBrokHash.get(i)){
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

        for(Broker b:getBrokers()){
            //System.out.println(b.getName()+" "+b.getHash());
            HashMap<Topic,ArrayList<Queue<Value>>> q = new HashMap<>();
            for(Topic t : b.getRelatedTopics()){
                //System.out.println(t.getBusLine());
                ArrayList<Queue<Value>> val = new ArrayList<>();;
                    q.put(t,val);
            }
            b.setQueueOfTopics(q);
        }

    }


	//returns list of brokers' hash
    private ArrayList<Long> allBrokerHash() {
        ArrayList<Long> allBrokHash = new ArrayList<>();
        if(!getBrokers().isEmpty()) {
            for (Broker b : getBrokers()) {
                allBrokHash.add(b.getHash());
            }
            Collections.sort(allBrokHash);
            return allBrokHash;
        }
        else{
            return null;
        }
    }



    private Long hashCode(String input) {
        try {
			//The Java MessageDigest class represents a cryptographic hash function which
			//can calculate a message digest from binary data.
			//Values returned by a hash function are called message digest, or hash values
			//xrhsimopoioume thn MD5 methodo
            MessageDigest md = MessageDigest.getInstance("MD5");
			
			/*
			// getting the status of MessageDigest object
            String str = md.toString();
			//print status: Status : MD5 Message Digest from SUN, <initialized>
			*/

            // digest() calculates message digest
            //  of an input digest() return array of byte
			//pairneis se pinaka apo theseis byte, to input string
			//GENIKA, to messageDigest pairnei to input ws pinaka apo byte kai  epistrefei ena MD5 hash instance
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
			//Translates the sign-magnitude representation of a BigInteger into a BigInteger (signed to big integer)
			//signum - signum of the number (-1 for negative, 0 for zero, 1 for positive).
			//magnitude - big-endian binary representation of the magnitude of the number.
            BigInteger no = new BigInteger(1, messageDigest);
            //return Math.abs(no.longValue());
            return no.longValue();
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


    }


    private HashMap<String, Long> calculateTopicHash() {
        List<Topic> topics = getTopics();
        HashMap<String, Long> topicHashes = new HashMap<>();
        for (Topic t : topics) {
            long h = hashCode(t.getBusLine());
            topicHashes.put(t.getBusLine(), h);
        }
        return topicHashes;
    }

    private void hashOfBrokers() {
		//brokers is a list of all available brokers
        List<Broker> brokers = getBrokers();
		//iterate the list of brokers
        for (Broker br : brokers) {
            //String input= this.getIp()+" "+Integer.toString(this.getPort());
			//foreach broker found in the list, split its ip string value and return it inside "parts" array
			//kathe thesi tou pinaka xwrizetai apo ta kommatia pou kathorizontai apo to regular expression
            String[] parts = br.getIp().split("\\.");
            String i="";
			//iterate the array that has just been generated, that includes the ip parts of the whole ip of specific broker
            //adds to string "i" the value of the positions of parts array, so a big string consisted of the whole ip of
			//the specific broker is generated
			for (int j = 0; j < parts.length; j++) {
                if(j==0)
                    i=parts[j];
                else
                    i=i+parts[j];
            }
			// i = sum of the characters of the ip, for ex: ip = 192.168.10.23 => i=1921681023
			
			//in is an integer that is the sum of the i (converted to integer) and the port of that broker 
            int in = Integer.parseInt(i) + br.getPort();
            String input = Integer.toString(in);
			//an h ip tou broker pou ektelei thn parousa synarthsh einai h idia me thn ip apo thn synoliki lista twn 
			//brokers, epishs an tautoxrona to port tou parontos broker einai to idio me ayto pou eksetazetai, tote vale 
			//ws hash timh aytou touu broker to apotelesma tou hash pou tha vgalei h synarthsh me to sygkekrimeno input
            if ((this.getIp().equals(br.getIp())) && (this.getPort() == br.getPort())) {
                this.hashBroker = hashCode(input);

            } else {
			//alliws vale ston broker pou exoume ayth th stigmh sto iteration, to apotelesma tou hash code tou input	
                br.setHash(hashCode(input));
            }
        }
    }

    
    void pull(String brokerName) throws SocketException {
        //byte[] buffer = new byte[1024]; //= mf.multimediaFileChunk;
        while(true){
            try{
                //receive
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);
                InetAddress inetAddress = datagramPacket.getAddress();
                int port = datagramPacket.getPort();
                String messageFromClient = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println("Message from client: " + messageFromClient);
                //send
                datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, port);
                datagramSocket.send(datagramPacket);
            }catch(IOException e){
                e.printStackTrace();
                break;
            }
        }
    }


    static class BrokerHandler implements Runnable {
        //Broker Handler is used to handle threats
        //will be needed to handle brokers
        private Socket client;

        public BrokerHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
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
