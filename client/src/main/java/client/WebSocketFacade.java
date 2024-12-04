package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private ServerMessageHandler serverMessageHandler;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    serverMessageHandler.handleMessage(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            // throw new ResponseException(500, ex.getMessage());
        }
    }
    public void makeMove(String authToken, int gameID, ChessMove move) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
        command.setMove(move);
        sendCommand(command);
    }
    public void leaveGame(String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        sendCommand(command);
    }
    public void resignGame(String authToken, int gameID) {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        sendCommand(command);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // This method is required by Endpoint, but we don't need to do anything here
    }

    public void sendCommand(UserGameCommand command) {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            // throw new ResponseException(500, ex.getMessage());
        }
    }

    public void disconnect() {
        try {
            this.session.close();
        } catch (IOException ex) {
            // throw new ResponseException(500, ex.getMessage());
        }
    }

    public interface ServerMessageHandler {
        void handleMessage(ServerMessage message);
    }

    private class ResponseException extends Throwable {
        public ResponseException(int i, String message) {
        }
    }
}
