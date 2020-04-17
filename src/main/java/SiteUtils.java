import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class SiteUtils {

    private final Queue<SiteContent> queueSiteContents;
    private final Queue<String> checkedUrl;

    public void addSiteContent(String url) throws IOException{
        Document document = getDocument(url);
        Set<String> links = findLinks(document, url);
        Set<String> keywords = findKeywords(document);
        String domainName = getDomainName(url);
        if(!checkedUrl.contains(url)){
            synchronized (queueSiteContents){
                queueSiteContents.add(new SiteContent(domainName, links, keywords));
            }
            synchronized (checkedUrl){
                checkedUrl.add(url);
            }
        }
    }

    private String getDomainName(String fullUrl){
        URL url = null;
        try {
            url = new URL(fullUrl);
        } catch (MalformedURLException e) {
        }
        return url.getHost();


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
