public class ConsumerThread extends Thread {
    private final SiteUtils siteUtils;
    public ConsumerThread(SiteUtils siteUtils) {
        this.siteUtils = siteUtils;
    }

    public void run(){

        while (true) {
            SiteContent siteContent;
            synchronized (siteUtils.getQueueSiteContents()) {
                if (siteUtils.getQueueSiteContents().isEmpty()) {
                    continue;
                }
                siteContent = siteUtils.getQueueSiteContents().poll();
            }
            try {
                String siteContentString = siteContent.toString();
                System.out.println("Consumer got item: " + siteContentString);
            }catch (Exception e){

            }
        }
    }
}
