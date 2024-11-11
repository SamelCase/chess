import chess.ChessGame;
import model.*;

import java.util.List;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        // Implement register API call
    }

    public AuthData login(String username, String password) throws Exception {
        // Implement login API call
    }

    public void logout(String authToken) throws Exception {
        // Implement logout API call
    }

    public void createGame(String gameName, String authToken) throws Exception {
        // Implement create game API call
    }

    public List<GameData> listGames(String authToken) throws Exception {
        // Implement list games API call
    }

    public void joinGame(int gameId, ChessGame.TeamColor playerColor, String authToken) throws Exception {
        // Implement join game API call
    }
}
