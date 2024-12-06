package client;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public class GameplayMessageHandler implements WebSocketFacade.ServerMessageHandler {
    private ChessGame.TeamColor playerColor;
    public GameplayMessageHandler(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }
    @Override
    public void handleMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME:
                handleGameUpdate(message.getGame().game());
                break;
            case ERROR:
                handleError(message.getErrorMessage());
                break;
            case NOTIFICATION:
                handleNotification(message.getMessage());
                break;
        }
    }
    private void handleGameUpdate(ChessGame game) {
        ChessBoardUI.drawBoard(game.getBoard(), playerColor == ChessGame.TeamColor.WHITE);
    }
    private void handleError(String errorMessage) {
        System.out.println("Error: " + errorMessage);
    }
    private void handleNotification(String notification) {
        System.out.println("Notification: " + notification);
    }
}