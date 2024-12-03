package client.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.UserGameCommand;
import webSocketMessages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private ServerMessageHandler serverMessageHandler;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {

        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // This method is required by Endpoint, but we don't need to do anything here
    }

    public void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void disconnect() throws ResponseException {
        try {
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public interface ServerMessageHandler {
        void handleServerMessage(ServerMessage message);
    }
}
