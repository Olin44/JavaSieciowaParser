import lombok.Getter;
import lombok.Setter;
/**
 *Klasa odpowiedzialna za przechowywanie parametrów startowych
 *
 */
@Getter
@Setter
public class StartParameters {
    private String startUrl;
    private int timeoutInMinutes;
}
