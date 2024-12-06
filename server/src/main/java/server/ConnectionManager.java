package server;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(String authToken, int gameID, Session session) {
        var connection = new Connection(authToken, gameID, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(int gameID, ServerMessage message, String authToken) throws IOException {
        var removeList = new ArrayList<Connection>();
        String jsonMessage = gson.toJson(message);
        for (var c : connections.values()) {
            if (c.session.isOpen() && c.gameID == gameID && !c.authToken.equals(authToken)) {
                c.send(jsonMessage);
            } else if (!c.session.isOpen()) {
                removeList.add(c);
            }
        }
        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

    private static class Connection {
        public String authToken;
        public int gameID;
        public Session session;

        public Connection(String authToken, int gameID, Session session) {
            this.authToken = authToken;
            this.gameID = gameID;
            this.session = session;
        }

        public void send(String message) throws IOException {
            session.getRemote().sendString(message);
        }
    }
}
