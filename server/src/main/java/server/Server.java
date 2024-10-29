package server;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.*;
import service.*;
import dataaccess.*;
import spark.*;
import com.google.gson.Gson;
import java.util.Map;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;
    private final Gson gson;
    public Server() {
        MemDataAccess mDAO = new MemDataAccess();
        userService = new UserService(mDAO);
        gameService = new GameService(mDAO);
        clearService = new ClearService(mDAO);
        gson = new Gson();
    }
    public int run(int desiredPort) throws DataAccessException {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        DatabaseInitializer.initializeDatabase();
        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.get("/game", this::listGamesHandler);
        Spark.post("/game", this::createGameHandler);
        Spark.put("/game", this::joinGameHandler);
        Spark.delete("/db",this::clearHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.exception(DataAccessException.class, this::exceptionHandler);
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }
    private Object joinGameHandler(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            if (authToken == null) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized2"));
            }
            var jsonBody = gson.fromJson(req.body(), Map.class);
            // Check if gameID exists and is of correct type
            Object gameIDObj = jsonBody.get("gameID");
            if (gameIDObj == null || !(gameIDObj instanceof Number)) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: bad request - missing or invalid gameID"));
            }
            int gameID = ((Number) gameIDObj).intValue();

            // Check if playerColor exists and is valid
            String playerColorStr = (String) jsonBody.get("playerColor");
            ChessGame.TeamColor playerColor;
            try {
                playerColor = ChessGame.TeamColor.valueOf(playerColorStr);
            } catch (IllegalArgumentException | NullPointerException e) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: bad request - invalid playerColor"));
            }

            gameService.joinGame(authToken, gameID, playerColor);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            switch (e.getMessage()) {
                case "Error: unauthorized":
                    res.status(401);
                    break;
                case "Error: already taken":
                    res.status(403);
                    break;
                case "Error: bad request":
                    res.status(400);
                    break;
                case "Error: Auth token not found":
                    res.status(401);
                    break;
                default:
                    res.status(500);
                    break;
            }
            return gson.toJson(Map.of("message", e.getMessage()));
        }
    }

    private Object createGameHandler(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized"));
            }
            var jsonBody = gson.fromJson(req.body(), Map.class);
            String gameName = (String) jsonBody.get("gameName");
            if (gameName == null || gameName.isEmpty()) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: bad request - missing game name"));
            }

            int gameID = gameService.createGame(authToken, gameName);
            // If successful, return 200 OK with the new game's ID
            res.status(200);
            return gson.toJson(Map.of("gameID", gameID));
        } catch (DataAccessException e) {
            // Handle unauthorized access
            res.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        }
    }

    private Object listGamesHandler(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            System.out.println(authToken);
            var games = gameService.listGames(authToken);
            res.status(200);
            return gson.toJson(Map.of("games", games));
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        }
    }
    private Object logoutHandler(Request req, Response res) {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized"));
            }
            userService.logout(authToken);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        }
    }


    private Object clearHandler(Request req, Response res) {
        try {
            clearService.clear();
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: clear failed"));
        }
    }
    private Object loginHandler(Request req, Response res) {
        try {
            UserData userData = gson.fromJson(req.body(), UserData.class);
            AuthData authData = userService.login(userData.username(), userData.password());
            res.status(200);
            return gson.toJson(authData);
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        }
    }
    private Object registerHandler(Request req, Response res) {
        try {
            UserData userData = gson.fromJson(req.body(), UserData.class);
            if (userData.username() == null || userData.password() == null || userData.email() == null) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: bad request - missing fields"));
            }
            AuthData authData = userService.register(userData);
            res.status(200);
            return gson.toJson(authData);
        } catch (DataAccessException e) {
            res.status(403);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private void exceptionHandler(Exception e, Request req, Response res) {
        res.status(500);
        res.body(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
