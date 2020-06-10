import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 *Klasa przechowująca linki do odwiedzonych stron.
 * Znajduje się w pole urls, w którym przechowywane są linki znalezione na odwiedzonej stronie, oraz słowa kluczowe;
 *
 */
@AllArgsConstructor
@ToString
@Setter
class SiteContent {
    @Getter
    private String domainUrl;
    private Set<String> urls;
    @Getter
    private String keywords;
    Set<String> getUrls() {
        if(urls.isEmpty()){
            return new HashSet<>();
        }
        return urls;
    }

}
