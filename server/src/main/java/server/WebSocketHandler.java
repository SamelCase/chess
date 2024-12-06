package server;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import chess.ChessGame;
import model.GameData;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess;
    private final Gson gson = new Gson();

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(command, session);
                case MAKE_MOVE -> handleMakeMove(command,session);
                case LEAVE -> handleLeave(command, session);
                case RESIGN -> handleResign(command,session);
            }
        } catch (ResponseException e) {
            sendErrorMessage(session, "errorMessage" + e.getMessage());
        }
    }
    private void handleConnect(UserGameCommand command, Session session) throws ResponseException, IOException {
        try {

            GameData game = dataAccess.getGame(command.getGameID());
            AuthData authData = dataAccess.getAuth(command.getAuthToken());
            if (authData == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            if (game == null) {
                throw new ResponseException(400, "Game not found");
            }
            connections.add(command.getAuthToken(), command.getGameID(), session);
            sendLoadGame(session, game);
            broadcastNotification(game.gameID(), command.getAuthToken() + " joined the game", command.getAuthToken());
        } catch (DataAccessException e) {
            sendErrorMessage(session, "Error connecting to game: " + e.getMessage());
        } catch (ResponseException e) {
            sendErrorMessage(session, "Error connecting to game: " + e.getMessage());
        }
    }
    private void handleMakeMove(UserGameCommand command, Session session) throws IOException {
        try {
            GameData game = dataAccess.getGame(command.getGameID());
            AuthData authData = dataAccess.getAuth(command.getAuthToken());
            if (game == null) {
                throw new ResponseException(400, "Game not found");
            }
            if (authData == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            // Check if the game is over
            if (game.game().isGameOver()) {
                throw new ResponseException(403, "Game is already over");
            }

            // Check if it's the player's turn
            ChessGame.TeamColor currentTurn = game.game().getTeamTurn();
            boolean isWhitePlayer = authData.username().equals(game.whiteUsername());
            boolean isBlackPlayer = authData.username().equals(game.blackUsername());
            if ((currentTurn == ChessGame.TeamColor.WHITE && !isWhitePlayer) ||
                    (currentTurn == ChessGame.TeamColor.BLACK && !isBlackPlayer)) {
                throw new ResponseException(403, "It's not your turn");
            }
            // Check if the player is an observer
            if (!isWhitePlayer && !isBlackPlayer) {
                throw new ResponseException(403, "Observers cannot make moves");
            }
            // Make the move
            game.game().makeMove(command.getMove());
            dataAccess.updateGame(game);
            broadcastLoadGame(game);
            broadcastNotification(game.gameID(), authData.username() + " made a move", command.getAuthToken());
            // Check for checkmate or stalemate
            if (game.game().isInCheckmate(game.game().getTeamTurn())) {
                broadcastNotification(game.gameID(), "Checkmate! " + authData.username() + " wins!", null);
            } else if (game.game().isInStalemate(game.game().getTeamTurn())) {
                broadcastNotification(game.gameID(), "Stalemate! The game is a draw.", null);
            }
        } catch (DataAccessException e) {
            sendErrorMessage(session, "Error updating game: " + e.getMessage());
        } catch (ResponseException e) {
            sendErrorMessage(session, e.getMessage());
        } catch (InvalidMoveException e) {
            sendErrorMessage(session,"Error illegal move: " + e.getMessage());
        }
    }
    private void handleLeave(UserGameCommand command, Session session) throws ResponseException, IOException {
        try {
            GameData game = dataAccess.getGame(command.getGameID());
            AuthData authData = dataAccess.getAuth(command.getAuthToken());
            if (authData.username().equals(game.blackUsername())) {
                game = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
            } else {
                game = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
            }
            connections.remove(command.getAuthToken());
            broadcastNotification(command.getGameID(), command.getAuthToken() + " left the game", command.getAuthToken());
            dataAccess.updateGame(game);
        } catch (DataAccessException e) {
            sendErrorMessage(session, "Error updating game: " + e.getMessage());
        }
    }
    private void handleResign(UserGameCommand command, Session session) throws ResponseException, IOException {
        try {
            GameData game = dataAccess.getGame(command.getGameID());
            AuthData authData = dataAccess.getAuth(command.getAuthToken());
            if (game == null) {
                throw new ResponseException(400, "Game not found");
            }
            if (authData == null) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            // Ensure the player is not an observer
            boolean isWhitePlayer = authData.username().equals(game.whiteUsername());
            boolean isBlackPlayer = authData.username().equals(game.blackUsername());
            if (!isWhitePlayer && !isBlackPlayer) {
                throw new ResponseException(403, "Observers cannot resign");
            }
            // Ensure the other player has not resigned
            if (game.game().getGameState() == ChessGame.GameState.RESIGNED) {
                throw new ResponseException(403, "The game has already been resigned");
            }
            // Set the game state to resigned
            game.game().setGameState(ChessGame.GameState.RESIGNED);
            dataAccess.updateGame(game);
            broadcastNotification(game.gameID(), authData.username() + " resigned from the game", null);
//            broadcastLoadGame(game);
        } catch (DataAccessException e) {
            sendErrorMessage(session, "Error updating game: " + e.getMessage());
        } catch (ResponseException e) {

            sendErrorMessage(session, e.getMessage());
        }
    }
    private void sendLoadGame(Session session, GameData game) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        message.setGame(game);
        session.getRemote().sendString(gson.toJson(message));
    }
    private void broadcastLoadGame(GameData game) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        message.setGame(game);
        connections.broadcast(game.gameID(), message,null);
    }
    private void broadcastNotification(int gameId, String notificationText, String authToken) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(notificationText);
        connections.broadcast(gameId, message,authToken);
    }
    private void sendErrorMessage(Session session, String errorMessage) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        message.setErrorMessage(errorMessage);
        session.getRemote().sendString(gson.toJson(message));
    }
}
