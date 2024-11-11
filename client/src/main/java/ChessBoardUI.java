import chess.*;

public class ChessBoardUI {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_WHITE_PIECE = "\u001B[97m";
    private static final String ANSI_BLACK_PIECE = "\u001B[30m";
    private static final String ANSI_WHITE_SQUARE = "\u001B[47m";
    private static final String ANSI_BLACK_SQUARE = "\u001B[0;100m";

    public static void drawBoard(ChessBoard board, boolean whiteBottom) {
        if (whiteBottom) {
            drawBoardWhiteBottom(board);
        } else {
            drawBoardBlackBottom(board);
        }
    }

    private static void drawBoardWhiteBottom(ChessBoard board) {
        // Implementation for drawing board with white at the bottom
    }

    private static void drawBoardBlackBottom(ChessBoard board) {
        // Implementation for drawing board with black at the bottom
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) return " ";

        String color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? ANSI_WHITE_PIECE : ANSI_BLACK_PIECE;

        switch (piece.getPieceType()) {
            case KING: return color + "♚" + ANSI_RESET;
            case QUEEN: return color + "♛" + ANSI_RESET;
            case ROOK: return color + "♜" + ANSI_RESET;
            case BISHOP: return color + "♝" + ANSI_RESET;
            case KNIGHT: return color + "♞" + ANSI_RESET;
            case PAWN: return color + "♟" + ANSI_RESET;
            default: return " ";
        }
    }
}
