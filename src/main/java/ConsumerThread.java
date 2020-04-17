import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.*;
import java.util.Queue;

@AllArgsConstructor
public class ConsumerThread extends Thread{
    private final Queue<SiteContent> queueSiteContents;

    public void run() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("C:\\Users\\Jacek\\Desktop\\chuj.json");
        while (true) {
            SiteContent siteContent;
            synchronized (queueSiteContents) {
                while (queueSiteContents.isEmpty()) {
                    try {
                        queueSiteContents.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                siteContent = queueSiteContents.poll();
            }
            System.out.println("save");
            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Jacek\\Desktop\\chuj.txt", true)))) {
                out.println(siteContent.toString());
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}