import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    final static BlockingQueue<SiteContent> queueSiteContents = new ArrayBlockingQueue<>(100);
    final static ArrayList<String> checkedUrl = new ArrayList<>();
    public static void main(String[] args) throws IOException{
        ProducerThread producerThread = new ProducerThread(queueSiteContents, checkedUrl, "https://boards.4chan.org/b");
        ConsumerThread consumerThread  = new ConsumerThread(queueSiteContents);
        producerThread.start();
        consumerThread.start();
    }

}
