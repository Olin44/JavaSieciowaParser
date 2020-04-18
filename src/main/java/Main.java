import java.util.*;


public class Main {
    private final static Set<SiteContent> queueSiteContents = new HashSet<>();
    private final static Set<String> checkedUrl = new HashSet<>();
    public static void main(String[] args){
          SitesScrapper sitesScrapper = new SitesScrapper(checkedUrl, queueSiteContents);
          sitesScrapper.addSiteContent("https://4chan.org/");
          }
}
