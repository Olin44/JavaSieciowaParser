import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class Main {
    private static final Random generator = new Random();
    private static final Queue<String> queue = new LinkedList<>();
    private static final
    List<String> checkedSites = new ArrayList<>();

    public static void main1(String[] args) {
        int itemCount = 5;
        Thread producer = new Thread(() -> {
            for (int i = 0; i < itemCount; i++) {
                try {
                    Thread.sleep(Duration.ofSeconds(generator.nextInt(5)).toMillis());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (queue) {
                    queue.add("Item no. " + i);
                    queue.notify();
                }
            }
        });
        Thread consumer = new Thread(() -> {
            int itemsLeft = itemCount;
            while (itemsLeft > 0) {
                String item;
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    item = queue.poll();
                }
                itemsLeft--;
                System.out.println("Consumer got item: " + item);
            }
        });
        consumer.start();
        producer.start();
    }

    final static Queue<SiteContent> queueSiteContents = new LinkedList<SiteContent>();
    final static Queue<String> checkedUrl = new LinkedList<>();
    public static void main(String[] args) throws IOException{
        SiteUtils siteUtils = new SiteUtils(queueSiteContents, checkedUrl);
        siteUtils.addSiteContent("https://www.samouczekprogramisty.pl/");

        ProducentThread producentThread = new ProducentThread(siteUtils);
        ConsumerThread consumerThread = new ConsumerThread(siteUtils);

        producentThread.start();
        consumerThread.start();
    }

}
