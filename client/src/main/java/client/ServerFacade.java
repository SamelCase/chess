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
        var request = new RegisterRequest(username, password, email);
        try {
            return this.makeRequest("POST", "/user", request, AuthData.class, null);
        } catch (Exception e) {
            if (e.getMessage().contains("already taken")) {
                throw new AlreadyTakenException();
            }
            throw new ServerFacadeException("Error: Unable to register");
        }
    }

    public AuthData login(String username, String password) throws ServerFacadeException {
        var request = new LoginRequest(username, password);
        try {
            return this.makeRequest("POST", "/session", request, AuthData.class, null);
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

    public void createGame(String gameName, String authToken) throws Exception {
        var path = "/game";
        var request = new CreateGameRequest(gameName);
        this.makeRequest("POST", path, request, null, authToken);
    }
    public void clearDB() throws Exception {
        var path = "/db";
        var request = new ClearDBRequest();
        this.makeRequest("DELETE", path, request, null, null);
    }
    public List<GameData> listGames(String authToken) throws Exception {
        var path = "/game";
        record ListGamesResponse(List<GameData> games) {}
        var response = this.makeRequest("GET", path, null, ListGamesResponse.class, authToken);
        return response.games();
    }

    public void joinGame(int gameId, ChessGame.TeamColor playerColor, String authToken) throws Exception {
        var path = "/game";
        var request = new JoinGameRequest(gameId, playerColor);

        this.makeRequest("PUT", path, request, null, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws Exception {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            // Set headers
            if (authToken != null && !authToken.isEmpty()) {
                http.setRequestProperty("Authorization", authToken);
            }
            if (request != null) {
                http.setRequestProperty("Content-Type", "application/json");
                String reqData = gson.toJson(request);
                try (OutputStream reqBody = http.getOutputStream()) {
                    reqBody.write(reqData.getBytes());
                }
            }
            // Read response
            http.connect();
            var status = http.getResponseCode();
            if (status != 200) {
                throw new Exception("Bad Request");
            }
            T response = null;
            if (responseClass != null) {
                try (InputStream respBody = http.getInputStream()) {
                    InputStreamReader reader = new InputStreamReader(respBody);
                    response = gson.fromJson(reader, responseClass);
                }
            }
            return response;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }
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
