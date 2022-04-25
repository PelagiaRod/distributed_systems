import java.util.List;

public class Node implements Runnable{
    List<Broker> brokers;

    void connect(){

    }
    void disconnect(){

    }

    void init (int port){

    }
    void updateNodes(){

    }

    @Override
    public void run(){

    }

}
/*
TRASH CODE
//MIGHT BE THE BASIC IDEA FOR init()
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("OK");
            //Lamda expression to implement runnable
            Runnable receiverLoopSubs = () -> {
                while (true) {
                    try {
                        Socket s = serverSocket.accept();
                        System.out.println("OK");
                        Consumer.ConsumerHandler handler = new Consumer.ConsumerHandler(s);
                        // Create a new Thread with this object.
                        Thread t = new Thread(handler);
                        // start the thread.
                        t.start();
                    } catch (IOException e){
                        return;
                    }
                }
            };
            Thread serverThreadSub = new Thread(receiverLoopSubs);

            serverThreadSub.start();
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("Failed to initialize subscriber at port " + port + ": " + e.getMessage());
        }
        try {
            ServerSocket serverSocket = new ServerSocket(port + 1);
            Runnable receiverLoopPubs = () -> {
                while (true) {
                    try {
                        Socket s = serverSocket.accept();
                        Publisher.PublisherHandler handler = new Publisher.PublisherHandler(s);
                        // Create a new Thread with this object.
                        Thread t = new Thread(handler);

                        // start the thread.
                        t.start();
                    } catch (IOException e) {
                        return;
                    }
                }
            };
            Thread serverThreadPub = new Thread(receiverLoopPubs);

            serverThreadPub.start();
        } catch (IOException e) {
            System.err.println("Failed to initialize publisher at port " + port+1 + ": " + e.getMessage());
        }

        try {
            ServerSocket serverSocket = new ServerSocket(port + 2);
            Runnable receiverLoopBroker = () -> {
                while (true) {
                    try {
                        Socket s = serverSocket.accept();
                        Broker.BrokerHandler handler = new Broker.BrokerHandler(s);
                        // Create a new Thread with this object.
                        Thread t = new Thread(handler);
                        t.start();
                    } catch (IOException e) {
                        return;
                    }
                }
            };
            Thread serverThreadBroker = new Thread(receiverLoopBroker);

            serverThreadBroker.start();
        } catch (IOException e) {
            System.err.println("Failed to initialize broker at port " + port+2 + ": " + e.getMessage());
        }
 */
