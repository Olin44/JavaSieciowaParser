import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@ToString
@Setter
public class SiteContent {
    @Getter
    private String domainUrl;
    private Set<String> urls;
    private Set<String> keywords;

    public Set<String> getUrls() {
        if(urls.isEmpty()){
            return new HashSet<>();
        }
        return urls;
    }

    public Set<String> getKeywords() {
        if(keywords.isEmpty()){
            return new HashSet<>();
        }
        return keywords;
    }
}
