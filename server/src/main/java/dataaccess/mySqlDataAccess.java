package dataaccess;

import chess.ChessGame;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.List;

import model.*;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() {
        // Constructor
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = getConnection()) {
            String[] clearStatements = {
                    "DELETE FROM auth_tokens",
                    "DELETE FROM games",
                    "DELETE FROM users"
            };

            try (Statement stmt = conn.createStatement()) {
                for (String clearStatement : clearStatements) {
                    stmt.executeUpdate(clearStatement);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.username());
            stmt.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
            stmt.setString(3, user.email());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }


    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
        return null;
    }


    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        // TODO: Implement createAuth method

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        // TODO: Implement getAuth method
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        // TODO: Implement deleteAuth method
    }

    @Override
    public String generateAuthToken() throws DataAccessException {
        return "";
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        // TODO: Implement createGame method
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        // TODO: Implement getGame method
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        // TODO: Implement listGames method
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        // TODO: Implement updateGame method
    }
    private Connection getConnection() throws DataAccessException {
        return DatabaseManager.getConnection();
    }
}
