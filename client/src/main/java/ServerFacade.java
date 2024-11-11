
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import chess.ChessGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson;

    public ServerFacade(String url) {
        serverUrl = url;
        gson = new Gson();
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var path = "/user";
        var request = new RegisterRequest(username, password, email);
        return this.makeRequest("POST", path, request, AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        var path = "/session";
        var request = new LoginRequest(username, password);
        return this.makeRequest("POST", path, request, AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, authToken);
    }

    public void createGame(String gameName, String authToken) throws Exception {
        var path = "/game";
        var request = new CreateGameRequest(gameName);
        this.makeRequest("POST", path, request, null, authToken);
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

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String... headers) throws Exception {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            // Set headers
            for (int i = 0; i < headers.length; i += 2) {
                http.setRequestProperty(headers[i], headers[i + 1]);
            }

            // Send request body
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
                throw new Exception("failure: " + status);
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

    private record RegisterRequest(String username, String password, String email) {}
    private record LoginRequest(String username, String password) {}
    private record CreateGameRequest(String gameName) {}
    private record JoinGameRequest(int gameID, ChessGame.TeamColor playerColor) {}
}
