package client;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public class GameplayMessageHandler implements WebSocketFacade.ServerMessageHandler {
    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }
    private ChessGame.TeamColor playerColor = ChessGame.TeamColor.WHITE;
    public GameplayMessageHandler() {
        }
    @Override
    public void handleMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME:
                doGameUpdateHandle(message.getGame().game());
                break;
            case ERROR:
                doErrorHandle(message.getErrorMessage());
                break;
            case NOTIFICATION:
                doNotificationHandle(message.getMessage());
                break;
        }
    }
    private void doGameUpdateHandle(ChessGame game) {
        ChessBoardUI.drawBoard(game.getBoard(), playerColor == ChessGame.TeamColor.WHITE);
    }
    private void doErrorHandle(String errorMessage) {
        System.out.println("Error: " + errorMessage);
    }
    private void doNotificationHandle(String notification) {
        System.out.println("Notification: " + notification);
    }
}