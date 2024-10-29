package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            createTables(conn);
        } catch (SQLException e) {
            throw new DataAccessException("Error initializing database", e);
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        String[] createTableStatements = {
                // Add your CREATE TABLE statements here, based on your schema.sql file
                "CREATE TABLE IF NOT EXISTS users ( ... )",
                "CREATE TABLE IF NOT EXISTS games ( ... )",
                "CREATE TABLE IF NOT EXISTS auth_tokens ( ... )"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String createTableStatement : createTableStatements) {
                stmt.executeUpdate(createTableStatement);
            }
        }
    }
}
