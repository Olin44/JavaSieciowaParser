import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class Main {
    final static Queue<SiteContent> queueSiteContents = new LinkedList<>();
    final static ArrayList<String> checkedUrl = new ArrayList<>();
    public static void main(String[] args) throws IOException{
        ProducentThread producentThread = new ProducentThread(queueSiteContents, checkedUrl, "https://boards.4chan.org/b");
        ConsumerThread consumerThread  = new ConsumerThread(queueSiteContents);
        producentThread.start();
        consumerThread.start();
    }

}
