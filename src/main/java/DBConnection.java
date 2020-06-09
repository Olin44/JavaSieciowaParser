import java.sql.*;

public class DBConnection {

    private Connection conn;
    private Statement statement;
    private PreparedStatement preper;
    private ResultSet result;

    public DBConnection() {
        conn = null;
        statement = null;
        preper = null;
        result = null;
    }

    public void connect() {
        String url;
        try {
            url = "jdbc:postgresql://localhost:5432/linki";
            System.setProperty("jdbc.Drivers", "org.postgresql.Drivers");
            conn = DriverManager.getConnection(url, "postgres", "admin");
            if (conn != null) {
                System.out.println("Start connection to database");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (!conn.isClosed()) {
                conn.close();
                System.out.println("Connection to database closed");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void createTables() {
        try {
            statement = conn.createStatement();
            String sql;
            sql = "create table if not exists valid_links" +
                    "(id serial, " +
                    "url text, " +
                    "keywords text, " +
                    "date timestamp default now()," +
                    "primary key(id))";
            if (!statement.execute(sql)) {
                System.out.println("Table valid_links add to database");
            }
            sql = "create table if not exists invalid_links" +
                    "(id serial, " +
                    "url text, " +
                    "exception text, " +
                    "day timestamp default now()," +
                    "primary key(id))";
            if (!statement.execute(sql)) {
                System.out.println("Table invalid_links add to database");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void dropTables() {
        try {
            statement = conn.createStatement();
            statement.execute("drop table if exists valid_links");
            statement.execute("drop table if exists invalid_links");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addLinkToValidLinks(String link, String keywords) {
        try {
            preper = conn.prepareStatement("insert into valid_links values (default, ?, ?)");
            preper.setString(1, link);
            preper.setString(2, keywords);
            preper.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void addLinkToInvalidLinks(String link, String exception) {
        try {
            preper = conn.prepareStatement("insert into invalid_links values (default, ?, ?)");
            preper.setString(1, link);
            preper.setString(2, exception);
            preper.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public String getAllTabSize() {
        int validLinksCount = 0;
        int invalidLinksCount = 0;
        try {
            invalidLinksCount = getInvalidLinksCount();
            validLinksCount = getValidLinksCount();
            return "Invalid links: " + invalidLinksCount + ", " + "Valid links: " + validLinksCount;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Invalid links: " + invalidLinksCount + ", " + "Valid links: " + validLinksCount;
    }

    public String getInvalidLinksStatistic() {
        int invalidLinksWithoutKeywors = 0;
        int invalidLinksCount = 0;
        int linksFromCheckedDomain = 0;
        int invalidLinksWithoutLinksOnSite = 0;
        try {
            invalidLinksCount = getInvalidLinksCount();
            linksFromCheckedDomain = getInvalidLinksDuplicateUrl();
            invalidLinksWithoutKeywors = getInvalidLinksWithoutKeywords();
            invalidLinksWithoutLinksOnSite = getInvalidLinksWithoutLinksOnSite();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int errorWithConnectionLinks = invalidLinksCount
                - invalidLinksWithoutKeywors
                - linksFromCheckedDomain
                - invalidLinksWithoutLinksOnSite;

        return "Invalid links: " + invalidLinksCount + ", \n" +
                "Invalid - links without keywords: " + invalidLinksWithoutKeywors + ", \n" +
                "Invalid - links from checked domains: " + linksFromCheckedDomain + ", \n" +
                "Invalid - links without links on site " + invalidLinksWithoutLinksOnSite +", \n" +
                "Invalid - links that could not be connected " + errorWithConnectionLinks  +".";
    }

    private int getInvalidLinksCount() throws SQLException {
        statement = conn.createStatement();
        String sql = "select count(*) from public.invalid_links";
        result = statement.executeQuery(sql);
        result.next();
        return result.getInt(1);
    }

    private int getValidLinksCount() throws SQLException {
        String sql = "select count(*) from valid_links";
        result = statement.executeQuery(sql);
        result.next();
        return result.getInt(1);
    }

    private int getInvalidLinksWithoutKeywords() throws SQLException {
        preper = conn.prepareStatement("select count(*) from invalid_links where invalid_links.exception = 'No keywords in meta section'");
        result = preper.executeQuery();
        result.next();
        return result.getInt(1);
    }

    private int getInvalidLinksDuplicateUrl() throws SQLException {
        preper = conn.prepareStatement("select count(*) from invalid_links where invalid_links.exception = 'Url from checked domain'");
        result = preper.executeQuery();
        result.next();
        return result.getInt(1);
    }

    private int getInvalidLinksWithoutLinksOnSite() throws SQLException {
        preper = conn.prepareStatement("select count(*) from invalid_links where invalid_links.exception = '0 links on site'");
        result = preper.executeQuery();
        result.next();
        return result.getInt(1);
    }
}
