package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import chess.ChessGame;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson;
    public ServerFacade(String url) {
        serverUrl = url;
        gson = new Gson();
    }
    public AuthData register(String username, String password, String email) throws ServerFacadeException {
        var regReq = new RegisterRequest(username, password, email);
        try {
            return this.makeRequest("POST", "/user", regReq, AuthData.class, null);
        } catch (Exception e) {
            if (e.getMessage().contains("already taken")) {
                throw new AlreadyTakenException();
            }
            throw new ServerFacadeException("Error: Unable to register");
        }
    }
    public WebSocketFacade createWebSocket(String gameID, WebSocketFacade.ServerMessageHandler handler) throws WebSocketException {
        String websocketUrl = serverUrl.replace("http", "ws") + "/connect";
        return new WebSocketFacade(websocketUrl, handler);
    }


    public AuthData login(String username, String password) throws ServerFacadeException {
        var loginReq = new LoginRequest(username, password);
        try {
            return this.makeRequest("POST", "/session", loginReq, AuthData.class, null);
        } catch (Exception e) {
            if (e.getMessage().contains("unauthorized")) {
                throw new UnauthorizedException();
            }
            throw new ServerFacadeException("Error: Unable to login");
        }
    }
    public void logout(String authToken) throws ServerFacadeException {
        try {
            this.makeRequest("DELETE", "/session", null, null, authToken);
        } catch (Exception e) {
            if (e.getMessage().contains("unauthorized")) {
                throw new UnauthorizedException();
            }
            throw new ServerFacadeException("Error: Unable to logout");
        }
    }
    public void createGame(String gameName, String authToken) throws ServerFacadeException {
        try {
            this.makeRequest("POST", "/game", new CreateGameRequest(gameName), null, authToken);
        } catch (Exception e) {
            if (e.getMessage().contains("unauthorized")) {
                throw new UnauthorizedException();
            }
            throw new ServerFacadeException("Error: Unable to create game");
        }
    }
    public void clearDB() throws ServerFacadeException {
        try {
            this.makeRequest("DELETE", "/db", new ClearDBRequest(), null, null);
        } catch (Exception e) {
            throw new ServerFacadeException("Error: Unable to clear database");
        }
    }
    public List<GameData> listGames(String authToken) throws ServerFacadeException {
        record ListGamesResponse(List<GameData> games) {}
        try {
            var response = this.makeRequest("GET", "/game", null, ListGamesResponse.class, authToken);
            return response.games();
        } catch (Exception e) {
            if (e.getMessage().contains("unauthorized")) {
                throw new UnauthorizedException();
            }
            throw new ServerFacadeException("Error: Unable to list games");
        }
    }
    public void joinGame(int gameId, ChessGame.TeamColor playerColor, String authToken) throws ServerFacadeException {
        var joinReq = new JoinGameRequest(gameId, playerColor);
        try {
            this.makeRequest("PUT", "/game", joinReq, null, authToken);
        } catch (Exception e) {
            if (e.getMessage().contains("unauthorized")) {
                throw new UnauthorizedException();
            }
            if (e.getMessage().contains("already taken")) {
                throw new AlreadyTakenException();
            }
            throw new ServerFacadeException("Error: Unable to join game");
        }
    }
    private <T> T makeRequest(String method, String path, Object req, Class<T> respClass, String authToken) throws Exception {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            // Set headers
            if (authToken != null && !authToken.isEmpty()) {
                http.setRequestProperty("Authorization", authToken);
            }
            if (req != null) {
                http.setRequestProperty("Content-Type", "application/json");
                String reqData = gson.toJson(req);
                try (OutputStream reqBody = http.getOutputStream()) {
                    reqBody.write(reqData.getBytes());
                }
            }
            // Read response
            http.connect();
            var status = http.getResponseCode();
            if (status == 401) {
                throw new UnauthorizedException();
            } else if (status == 403) {
                throw new AlreadyTakenException();
            } else if (status != 200) {
                throw new Exception("Bad Request");
            }
            T response = null;
            if (respClass != null) {
                try (InputStream respBody = http.getInputStream()) {
                    InputStreamReader reader = new InputStreamReader(respBody);
                    response = gson.fromJson(reader, respClass);
                }
            }
            return response;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }
    // Specific Errors
    public class ServerFacadeException extends Exception {
        public ServerFacadeException(String message) {
            super(message);
        }
    }
    public class UnauthorizedException extends ServerFacadeException {
        public UnauthorizedException() {
            super("Error: unauthorized");
        }
    }
    public class AlreadyTakenException extends ServerFacadeException {
        public AlreadyTakenException() {
            super("Error: already taken");
        }
    }
    private record RegisterRequest(String username, String password, String email) {}
    private record LoginRequest(String username, String password) {}
    private record CreateGameRequest(String gameName) {}
    private record JoinGameRequest(int gameID, ChessGame.TeamColor playerColor) {}
    private record ClearDBRequest() {}
}
