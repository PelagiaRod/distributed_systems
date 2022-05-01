import java.net.Socket;

//subscriber service
public class Consumer implements Runnable {
    ProfileName subscriber;

    public static void main(String[] args)
    {
    }

    public Consumer() {
    }

    public Consumer(ProfileName subscriber)
    {
        this.subscriber = subscriber;
    }

    @Override
    public void run(){
    }

    void disconnect(String topic){
        // unsibscribe
    }

    void register (String topic){
        //subscribe
    }
    void showConversationData(String topic, Value val){

    }

    public static class ConsumerHandler implements Runnable {
        Socket connection;

        public ConsumerHandler(Socket connection) {
            this.connection = connection;
        }

        public void run() {

        }
    }

}
/*
        TRASH CODE
        try{
            Socket client = new Socket("127.0.0.1", 1234);

            String str = "Gigi";

            OutputStreamWriter os = new OutputStreamWriter(client.getOutputStream());
            PrintWriter out = new PrintWriter(os);
            out.println(str);
            os.flush();

            ConsumerHandler cHandler = new ConsumerHandler(client);
            Thread t = new Thread(cHandler);
            t.start();
            //out = new ObjectOutputStream(client.getOutputStream());
            //in = new ObjectInputStream(client.getInputStream());

           //ConsumerHandler cHandler = new ConsumerHandler(client);
           //Thread t = new Thread(cHandler);
           //t.start();
        } catch (IOException e){
            e.printStackTrace();
        }
         try {
                //out = new ObjectOutputStream(connection.getOutputStream());
                //in = new ObjectInputStream(connection.getInputStream());
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (true)
                {
                    String message = inReader.readLine();
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        */
