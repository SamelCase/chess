package server;
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
import java.util.Collection;

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
            if (game == null) {
                throw new ResponseException(400, "Game not found");
            }
//            if (command.getMove())
//            game.game().makeMove(command.getMove());
            // TODO: Make sure game isn't over before making a move;
            // Players shouldn't be able to move pieces after checkmate or stalemate
            // TODO: Make sure move is valid.
            // Players shouldn't be able to make an invalid move
            // TODO: Make sure move is from the player whos turn it is.
            // Players shouldn't be able to move pieces while its not their turn, or move opponent's pieces
            // TODO: Validate auth token
            // Players shouldn't be able to make a move if not in the game, or if an observer
            // TODO Make sure player isn't resigned
            // Players shouldn't be able to move after resignation

            dataAccess.updateGame(game);
            broadcastLoadGame(game);
            broadcastNotification(game.gameID(), command.getAuthToken() + " made a move", command.getAuthToken());
        } catch (DataAccessException e) {
            sendErrorMessage(session, "Error updating game: " + e.getMessage());
        } catch (ResponseException e) {
            sendErrorMessage(session, "Error updating game: " + e.getMessage());
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
            // TODO: Ensure is not observer
            // Observers should not be able to resign
            // TODO: Ensure other player has not resigned
            // Players shouldn't be able to resign after their opponent has resigned
            GameData game = dataAccess.getGame(command.getGameID());
            if (game == null) {
                throw new ResponseException(400, "Game not found");
            }
            broadcastNotification(game.gameID(), command.getAuthToken() + " resigned from the game", command.getAuthToken());
        }
        catch (DataAccessException e) {
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
