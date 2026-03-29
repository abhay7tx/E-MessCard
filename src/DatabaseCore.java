import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Shared database connection helper.
 */
public class DatabaseCore {

    private static final String DB_URL = "jdbc:sqlite:messsystem.db";

    public static Connection connect() throws SQLException {
        Connection c = DriverManager.getConnection(DB_URL);
        c.createStatement().execute("PRAGMA foreign_keys = ON");
        return c;
    }
}
