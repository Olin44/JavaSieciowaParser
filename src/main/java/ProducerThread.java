import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

@Getter
@Setter
public class ProducerThread extends Thread {
    private static final Random generator = new Random();
    private final BlockingQueue<SiteContent> queueSiteContents;
    private final ArrayList<String> checkedUrl;
    private final String url;

    ProducerThread(BlockingQueue<SiteContent> queueSiteContents, ArrayList<String> checkedUrl, String url){
        this.queueSiteContents = queueSiteContents;
        this.checkedUrl = checkedUrl;
        this.url = url;
    }

    public void run(){
        try {
            addSiteContent(url);
            while (true){
            try {
                for(String s : queueSiteContents.take().getUrls()){
                    addSiteContent(s);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addSiteContent(String url) throws IOException {
        try {
            Document document = getDocument(url);
            Set<String> links = findLinks(document, url);
            Set<String> keywords = findKeywords(document);
            if (!checkedUrl.contains(url)) {
                try {
                    queueSiteContents.put(new SiteContent(url, links, keywords));
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e ) {
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
