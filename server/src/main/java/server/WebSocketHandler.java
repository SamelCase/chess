package server;
import com.google.gson.Gson;
import dataaccess.DataAccess;
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
                case MAKE_MOVE -> handleMakeMove(command);
                case LEAVE -> handleLeave(command);
                case RESIGN -> handleResign(command);
            }
        } catch (ResponseException e) {
            sendErrorMessage(session, e.getMessage());
        }
    }

    private void handleConnect(UserGameCommand command, Session session) throws ResponseException, IOException {
        GameData game = dataAccess.getGame(command.getGameID());
        if (game == null) {
            throw new ResponseException(400, "Game not found");
        }
        connections.add(command.getAuthToken(), session);
        sendLoadGame(session, game);
        broadcastNotification(game.gameID(), command.getAuthToken() + " joined the game");
    }

    private void handleMakeMove(UserGameCommand command) throws ResponseException, IOException {
        GameData game = dataAccess.getGame(command.getGameID());
        if (game == null) {
            throw new ResponseException(400, "Game not found");
        }
        // Implement move validation and game state update here
        // For now, we'll just update the game and broadcast the new state
        dataAccess.updateGame(game);
        broadcastLoadGame(game);
        broadcastNotification(game.gameID(), command.getAuthToken() + " made a move");
    }

    private void handleLeave(UserGameCommand command) throws ResponseException, IOException {
        connections.remove(command.getAuthToken());
        broadcastNotification(command.getGameID(), command.getAuthToken() + " left the game");
    }

    private void handleResign(UserGameCommand command) throws ResponseException, IOException {
        GameData game = dataAccess.getGame(command.getGameID());
        if (game == null) {
            throw new ResponseException(400, "Game not found");
        }
        // Implement game resignation logic here
        // For now, we'll just broadcast the resignation
        broadcastNotification(game.gameID(), command.getAuthToken() + " resigned from the game");
    }

    private void sendLoadGame(Session session, GameData game) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        message.setGame(game);
        session.getRemote().sendString(gson.toJson(message));
    }

    private void broadcastLoadGame(GameData game) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        message.setGame(game);
        connections.broadcast(game.gameID(), message);
    }

    private void broadcastNotification(int gameId, String notificationText) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(notificationText);
        connections.broadcast(gameId, message);
    }

    private void sendErrorMessage(Session session, String errorMessage) throws IOException {
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        message.setMessage(errorMessage);
        session.getRemote().sendString(gson.toJson(message));
    }
}