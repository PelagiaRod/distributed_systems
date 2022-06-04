
import java.io.File;
import java.util.ArrayList;

import helpers.FileHelper;
import models.Topic;

public final class LoadData {
    private static File currDirectory = new File(new File("").getAbsolutePath());
    private static String topicsPath = currDirectory + "\\data\\Topics.txt";
    private static String brokersPath = currDirectory + "\\data\\Brokers.txt";

    public static ArrayList<Topic> loadTopics() {
        ArrayList<Topic> topics = new ArrayList<>();
        ArrayList<String> topicsLines = FileHelper.readFile(topicsPath);
        for (String line : topicsLines) {
            topics.add(new Topic(line));
        }
        return topics;
    }

    public static ArrayList<Broker> loadBrokers() {
        ArrayList<String> brokersLines = FileHelper.readFile(brokersPath);
        ArrayList<Broker> brokers = new ArrayList<>();
        System.out.println("--Brokers--");
        for (String line : brokersLines) {
            System.out.println(line);
            String[] data = line.split(" , ");
            brokers.add(new Broker(data[0], data[1], Integer.parseInt(data[2])));
        }
        return brokers;
    }
}
