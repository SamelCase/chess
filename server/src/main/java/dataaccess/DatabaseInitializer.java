package dataaccess;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() throws DataAccessException {
        createDatabase();
        createTables();
    }

    private static void createDatabase() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new DataAccessException("Error creating database: " + e.getMessage());
        }
    }

    private static void createTables() throws DataAccessException {
        String[] createTableStatements = {
                "CREATE TABLE IF NOT EXISTS users (user_id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) UNIQUE NOT NULL, password VARCHAR(255) NOT NULL, email VARCHAR(100) UNIQUE NOT NULL)",
                "CREATE TABLE IF NOT EXISTS games (game_id INT AUTO_INCREMENT PRIMARY KEY, game_name VARCHAR(100) NOT NULL, white_username VARCHAR(50), black_username VARCHAR(50), game_state TEXT NOT NULL)",
                "CREATE TABLE IF NOT EXISTS auth_tokens (auth_token VARCHAR(255) PRIMARY KEY, username VARCHAR(50) NOT NULL)"
        };

        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                for (String createTableStatement : createTableStatements) {
                    stmt.executeUpdate(createTableStatement);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating tables: " + e.getMessage());
        }
    }
}
