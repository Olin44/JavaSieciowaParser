import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import static org.apache.logging.log4j.Level.ALL;

public class Main {
    private static DBConnection dbConnection;
    public static void main(String[] args) {
        configureLogger();
        dbConnection = prepareDBConnection();
        StartParameters startParameters = prepareStartParameters();
        SitesScrapperThread sitesScrapperThread = new SitesScrapperThread(startParameters, dbConnection);
        sitesScrapperThread.run();
        createSummary();
        dbConnection.disconnect();
    }
    /**
     *Metoda odpowiedzialna za przygotowanie połączenie do bazy danych.
     *
     */
    private static DBConnection prepareDBConnection(){
        DBConnection dbConnection = new DBConnection();
        dbConnection.connect();
        dbConnection.dropTables();
        dbConnection.createTables();
        return dbConnection;
    }
    /*
    *Metoda odpowiedzialna za przygotowanie parametrów startowych
    *
     */
    private static StartParameters prepareStartParameters(){
        StartParameters startParameters = new StartParameters();
        startParameters.setStartUrl("https://www.whitepress.pl/baza-wiedzy/41/jak-wybrac-najlepsze-slowa-kluczowe-do-pozycjonowania");
        startParameters.setTimeoutInMinutes(1);
        return startParameters;
    }
    /**
     *Metoda odpowiedzialna za przygotowanie parametrów startowych.
     *
     */
    private static void createSummary(){
        String tablesSizes = dbConnection.getAllTabSize();
        String invalidLinksStatistics = dbConnection.getInvalidLinksStatistic();
        System.out.println(tablesSizes + "\n" + invalidLinksStatistics);
    }
    /**
     *Metoda odpowiedzialna za konfigurację loggera.
     *
     */
    private static void configureLogger(){
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(ALL);
        ctx.updateLoggers();

    }
}
