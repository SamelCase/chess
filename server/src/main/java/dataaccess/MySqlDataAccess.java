package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            stmt.setString(2, user.password());
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
        String sql = "INSERT INTO auth_tokens (auth_token, username) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth token: " + e.getMessage());
        }
    }


    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM auth_tokens WHERE auth_token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    return new AuthData(authToken, username);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting auth token: " + e.getMessage());
        }
        return null;
    }
    public boolean verifyPassword(String username, String password) throws DataAccessException {
        UserData user = getUser(username);
        if (user == null) {
            return false;
        }
        return BCrypt.checkpw(password, user.password());
    }


    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth_tokens WHERE auth_token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }


    @Override
    public String generateAuthToken() throws DataAccessException {
        return UUID.randomUUID().toString();
    }
    @Override
    public int createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO games (game_name, white_username, black_username, game_state) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, game.gameName());
            stmt.setString(2, game.whiteUsername());
            stmt.setString(3, game.blackUsername());
            String gameState = new Gson().toJson(game.game());
            stmt.setString(4, gameState);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DataAccessException("Creating game failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT game_id, game_name, white_username, black_username, game_state FROM games WHERE game_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String gameName = rs.getString("game_name");
                    String whiteUsername = rs.getString("white_username");
                    String blackUsername = rs.getString("black_username");
                    String gameState = rs.getString("game_state");
                    ChessGame game = new Gson().fromJson(gameState, ChessGame.class);
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting game: " + e.getMessage());
        }
        return null;
    }
    @Override
    public List<GameData> listGames() throws DataAccessException {
        String sql = "SELECT game_id, game_name, white_username, black_username, game_state FROM games";
        List<GameData> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int gameID = rs.getInt("game_id");
                String gameName = rs.getString("game_name");
                String whiteUsername = rs.getString("white_username");
                String blackUsername = rs.getString("black_username");
                String gameState = rs.getString("game_state");
                ChessGame game = new Gson().fromJson(gameState, ChessGame.class);
                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }
        return games;
    }


    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE games SET white_username = ?, black_username = ?, game_state = ? WHERE game_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            String gameState = new Gson().toJson(game.game());
            stmt.setString(3, gameState);
            stmt.setInt(4, game.gameID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    private Connection getConnection() throws DataAccessException {
        return DatabaseManager.getConnection();
    }
}
