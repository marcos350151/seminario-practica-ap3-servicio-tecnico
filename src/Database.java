import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String URL = System.getenv().getOrDefault(
            "ST_DB_URL", "jdbc:mysql://localhost:3306/servicio_tecnico?useSSL=false&serverTimezone=UTC");
    private static final String USER = System.getenv().getOrDefault("ST_DB_USER", "root");
    private static final String PASS = System.getenv().getOrDefault("ST_DB_PASS", "");

    private Database() {}

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
