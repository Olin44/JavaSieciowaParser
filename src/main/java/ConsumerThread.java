import lombok.AllArgsConstructor;

import java.io.*;
import java.util.concurrent.BlockingQueue;

@AllArgsConstructor
public class ConsumerThread extends Thread{
    private final BlockingQueue<SiteContent> queueSiteContents;

    public void run() {
        try{
            SiteContent siteContent;
            while(true){
                siteContent = queueSiteContents.take();
                try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Jacek\\Desktop\\chuj.txt", true)))) {
                    out.println(siteContent.toString());
                    System.out.println(siteContent.toString());
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}