import java.io.IOException;
import java.util.Queue;

public class ProducentThread extends Thread {
    private final SiteUtils siteUtils;
    public ProducentThread(SiteUtils siteUtils) {
        this.siteUtils = siteUtils;
    }

    public void run(){
        while (true) {
            synchronized (siteUtils) {
                for(String string : siteUtils.getQueueSiteContents().poll().getUrls()){
                    try {
                        siteUtils.addSiteContent(string);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



//
//    public void run(){
//        for (int i = 0; i < itemCount; i++) {
//            try {
//                Thread.sleep(Duration.ofSeconds(generator.nextInt(5)).toMillis());
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            synchronized (queue) {
//                queue.add("Item no. " + i);
//                queue.notify();
//            }
//        }
//    }
}
