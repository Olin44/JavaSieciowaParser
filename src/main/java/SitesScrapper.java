import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
class SitesScrapper {
    private Set<String> checkedUrl;
    private Set<SiteContent> siteContents;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    SitesScrapper(Set<String> checkedUrl, Set<SiteContent> siteContents) {
        this.checkedUrl = checkedUrl;
        this.siteContents = siteContents;
    }

    void addSiteContent(String url){
        try {
            Document document = getDocument(url);
            Set<String> links = findLinks(document, url);
            Set<String> keywords = findKeywords(document);
            SiteContent siteContent;
            if (!checkedUrl.contains(url) && !keywords.isEmpty()) {
                siteContent = new SiteContent(url, links, keywords);
                saveSiteContent(siteContent);
                for (String nestedUrl : siteContent.getUrls()) {
                    addSiteContent(nestedUrl);
                }
            }
        }
        catch (Exception e ) {
            log.error(e.toString());
        }

    }

    private void saveSiteContent(SiteContent siteContent){
        siteContents.add(siteContent);
        checkedUrl.add(siteContent.getDomainUrl());
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/links.txt", true)))) {
            out.println(siteContent.toString());
        }
        catch (Exception e){
            log.error(e.toString());
        }
    }
    private Set<String> findLinks(Document doc, String url){

        Set<String> links = new HashSet<>();
        Elements elements = doc.select("a[href]");
        String foundUrl;
        for (Element element : elements) {
            foundUrl = element.attr("href");
            if(!foundUrl.startsWith(url) && foundUrl.startsWith("http")){
                links.add(foundUrl);
            }
        }
        if(links.isEmpty()){
            links.add("0 links on site");
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
            String keywordsFromSite = document.select("meta[name=keywords]").first().attr("content");
            keywords.add(keywordsFromSite);
        }catch (NullPointerException e){
            log.error("No keywords in meta section");
        }
        return keywords;
    }
}
