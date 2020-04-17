import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

@Getter
@Setter
public class ProducentThread extends Thread {
    private static final Random generator = new Random();
    private final Queue<SiteContent> queueSiteContents;
    private final ArrayList<String> checkedUrl;

    ProducentThread(Queue<SiteContent> queueSiteContents, ArrayList<String> checkedUrl, String url){
        this.queueSiteContents = queueSiteContents;
        this.checkedUrl = checkedUrl;
        try {
            addSiteContent(url);
        } catch (IOException e) {
        }
    }

    public void run(){
        while(true){
            System.out.println(queueSiteContents.size());
            SiteContent siteContent = queueSiteContents.element();
            if(siteContent != null) {
                for (String url : siteContent.getUrls()) {
                    if (!checkedUrl.contains(url)) {
                        try {
                            addSiteContent(url);
                        } catch (IOException e) {
                        }
                    }
                }
            }
            else{
                System.out.println(siteContent.toString());
            }
            synchronized (queueSiteContents){
                queueSiteContents.notify();
            }

        }
    }

    public void addSiteContent(String url) throws IOException{
        Document document = getDocument(url);
        Set<String> links = findLinks(document, url);
        Set<String> keywords = findKeywords(document);
        if(!checkedUrl.contains(url)){
            synchronized (queueSiteContents){
                queueSiteContents.add(new SiteContent(url, links, keywords));
                System.out.println("dodaje item");
            }
            checkedUrl.add(url);
        }
    }

    private Set<String> findLinks(Document doc, String url) throws IOException {

        Set<String> links = new HashSet<>();

        Elements elements = doc.select("a[href]");

        String foundUrl;
        for (Element element : elements) {
            foundUrl = element.attr("href");
            if(!foundUrl.startsWith(url) && foundUrl.startsWith("http")){
                links.add(foundUrl);
            }
        }
        return links;
    }

    private Document getDocument(String url) throws IOException{
        return Jsoup.connect(url)
                .data("query", "Java")
                .userAgent("Mozilla")
                .cookie("auth", "token")
                .timeout(30000)
                .get();
    }

    private Set<String> findKeywords(Document document){
        Set<String> keywords = new HashSet<>();
        try{
            String keywords1 = document.select("meta[name=keywords]").first().attr("content");
            keywords.add(keywords1);
        }catch (NullPointerException e){
        }
        return keywords;
    }
}
