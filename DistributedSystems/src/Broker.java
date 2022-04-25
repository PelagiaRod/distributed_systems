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
