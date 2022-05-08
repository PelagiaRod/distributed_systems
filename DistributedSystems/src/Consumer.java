import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

//subscriber service
public class Consumer implements Runnable {
    ProfileName subscriber;
    static Socket client;
    static String username;

    public Consumer() {
    }

    public Consumer(ProfileName subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void run() {
    }

    void disconnect(String topic) {
        // unsibscribe
    }

    void register(String topic) {
        // subscribe
    }

    void showConversationData(String topic, Value val) {

    }

    public static void main(String[] args) throws IOException {
        System.out.println("Please enter your name:");
        Scanner scanner = new Scanner(System.in);
        username = scanner.nextLine();
        System.out.println("Select a topic");
        Scanner myTopic = new Scanner(System.in);
        String subject = myTopic.nextLine();
        ArrayList<Topic> topics = Node.readTopicsList();

        for (Topic topic : topics) {
            if (topic.getChannelName().equals(subject)) {
                client = new Socket("127.0.0.1", 1234);
                pull(subject);
                break;
            }
        }

    }

    public static synchronized void pull(String subject) throws IOException {
        // readMessage thread
        DataInputStream dis = new DataInputStream(client.getInputStream());
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
        dos.writeUTF(username);
        dos.writeUTF(subject);

        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {

                        // read the message sent to this client
                        String msg = dis.readUTF();
                        System.out.println(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });
        readMessage.start();
    }

}
/*
 * TRASH CODE
 * try{
 * Socket client = new Socket("127.0.0.1", 1234);
 * 
 * String str = "Gigi";
 * 
 * OutputStreamWriter os = new OutputStreamWriter(client.getOutputStream());
 * PrintWriter out = new PrintWriter(os);
 * out.println(str);
 * os.flush();
 * 
 * ConsumerHandler cHandler = new ConsumerHandler(client);
 * Thread t = new Thread(cHandler);
 * t.start();
 * //out = new ObjectOutputStream(client.getOutputStream());
 * //in = new ObjectInputStream(client.getInputStream());
 * 
 * //ConsumerHandler cHandler = new ConsumerHandler(client);
 * //Thread t = new Thread(cHandler);
 * //t.start();
 * } catch (IOException e){
 * e.printStackTrace();
 * }
 * try {
 * //out = new ObjectOutputStream(connection.getOutputStream());
 * //in = new ObjectInputStream(connection.getInputStream());
 * BufferedReader inReader = new BufferedReader(new
 * InputStreamReader(System.in));
 * while (true)
 * {
 * String message = inReader.readLine();
 * System.out.println(message);
 * }
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 */
