package websocket.messages;

import chess.ChessGame;
import java.util.Objects;

public class ServerMessage {
    private final ServerMessageType serverMessageType;
    private String message;
    private ChessGame game;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessage(ServerMessageType type, String message) {
        this(type);
        this.message = message;
    }

    public ServerMessage(ServerMessageType type, ChessGame game) {
        this(type);
        this.game = game;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getMessage() {
        return message;
    }

    public ChessGame getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType() &&
                Objects.equals(getMessage(), that.getMessage()) &&
                Objects.equals(getGame(), that.getGame());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getMessage(), getGame());
    }
}
