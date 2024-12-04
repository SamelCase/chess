package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    private final ConcurrentLinkedQueue<String> notificationQueue = new ConcurrentLinkedQueue<>();
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
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                        notificationQueue.offer(serverMessage.getMessage());
                    }
                    serverMessageHandler.handleMessage(serverMessage);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            // throw new ResponseException(500, ex.getMessage());
        }
    }
    public boolean hasNotification() {
        return !notificationQueue.isEmpty();
    }
    public String getNextNotification() {
        return notificationQueue.poll();
    }
    public void sendGameCommand(UserGameCommand.CommandType commandType, String authToken, int gameID, ChessMove move) throws WebSocketException {
        UserGameCommand command = new UserGameCommand(commandType, authToken, gameID);
        if (commandType == UserGameCommand.CommandType.MAKE_MOVE) {
            command.setMove(move);
        }
        sendCommand(command);
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // This method is required by Endpoint, but we don't need to do anything here
    }
    public void sendCommand(UserGameCommand command) throws WebSocketException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new WebSocketException("Failed to send command", ex);
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
