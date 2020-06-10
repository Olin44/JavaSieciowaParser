import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
/**
 *Główny wątek aplikacji.
 *
 */
@Getter
@Setter
class SitesScrapperThread extends Thread {
    private final static Set<SiteContent> siteContents = new HashSet<>();
    private final static Set<String> checkedUrl = new HashSet<>();
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private StartParameters startParameters;
    private DBConnection dbConnection;
    private LocalTime finishTime;

    SitesScrapperThread(StartParameters startParameters, DBConnection dbConnection) {
        this.finishTime = LocalTime.now().plusMinutes(startParameters.getTimeoutInMinutes());
        this.startParameters = startParameters;
        this.dbConnection = dbConnection;
    }

    @Override
    public void run(){
        addSiteContent(startParameters.getStartUrl());
    }
    /**
     *metoda odwiedzająca rekurencyjnie kolejne adresy
     *
     */
    private void addSiteContent(String url){
        if(finishTime.isAfter(LocalTime.now())) {
            try {
                Document document = getDocument(url);
                Set<String> links = findLinks(document, url);
                String keywords = findKeywords(document);
                SiteContent siteContent;
                if (!checkedUrl.contains(url) && !keywords.isEmpty()) {
                    siteContent = new SiteContent(url, links, keywords);
                    saveSiteContent(siteContent);
                    for (String nestedUrl : siteContent.getUrls()) {
                        addSiteContent(nestedUrl);
                    }
                }
                if(checkedUrl.contains(url)){
                    dbConnection.addLinkToInvalidLinks(url, "Url from checked domain");
                    log.error("Invalid link: " + url + " " + "Url from checked domain");
                }
                if(keywords.isEmpty()){
                    dbConnection.addLinkToInvalidLinks(url, "No keywords in meta section");
                    log.error("Invalid link: " + url + " " + "No keywords in meta section");
                }
            } catch (Exception e) {
               dbConnection.addLinkToInvalidLinks(url, e.getMessage());
               log.error("Invalid link: " + url + " " + e.getMessage());
            }
        }
    }
    /**
     *metoda zapisująca poprawne adresy do bazy
     *
     */
    private void saveSiteContent(SiteContent siteContent){
        siteContents.add(siteContent);
        checkedUrl.add(siteContent.getDomainUrl());
        dbConnection.addLinkToValidLinks(siteContent.getDomainUrl(), siteContent.getKeywords());
        log.info("Valid link: " + siteContent.getDomainUrl() + " " + siteContent.getKeywords());
    }
    /**
     *metoda wyszukująca linki na stronie
     *
     */
    private Set<String> findLinks(Document doc, String url) throws SitesScrapperException{

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
            throw new SitesScrapperException("0 links on site");
            //links.add("0 links on site");
        }
        return links;
    }
    /**
     *metoda pobierająca zawartość strony
     *
     */
    private Document getDocument(String url) throws IOException{
        return Jsoup.connect(url)
                .data("query", "Java")
                .userAgent("Mozilla")
                .cookie("auth", "token")
                .timeout(30000)
                .get();
    }
    /**
     *metoda szukająca słów kluczowych
     *
     */
    private String findKeywords(Document document) throws SitesScrapperException{
        String keywords;
        try{
            keywords = document.select("meta[name=keywords]").first().attr("content");
        }catch (NullPointerException e){
            throw new SitesScrapperException("No keywords in meta section");
        }
        return keywords;
    }
}
