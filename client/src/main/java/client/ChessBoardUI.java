package client;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ChessBoardUI {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_WHITE_PIECE = "\u001B[97m";
    private static final String ANSI_BLACK_PIECE = "\u001B[30m";
    private static final String ANSI_WHITE_SQUARE = "\u001B[47m";
    private static final String ANSI_BLACK_SQUARE = "\u001B[0;100m";

    public static void drawBoard(ChessBoard board, boolean whiteBottom) {
        System.out.println(whiteBottom ? "White's Perspective:" : "Black's Perspective:");
        String[] files = whiteBottom ? new String[]{"a", "b", "c", "d", "e", "f", "g", "h"} : new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
        int startRank = whiteBottom ? 8 : 1;
        int endRank = whiteBottom ? 1 : 8;
        int rankStep = whiteBottom ? -1 : 1;

        // Print column labels
        System.out.print("   ");
        for (String file : files) {
            System.out.print(file + " \u2003");
        }
        System.out.println();

        for (int rank = startRank; whiteBottom ? rank >= endRank : rank <= endRank; rank += rankStep) {
            System.out.print(rank + " ");
            for (int file = 0; file < 8; file++) {
                ChessPosition position = new ChessPosition(rank, file + 1);
                ChessPiece piece = board.getPiece(position);
                String square = (rank + file) % 2 == 0 ? ANSI_WHITE_SQUARE : ANSI_BLACK_SQUARE;
                System.out.print(square + getPieceSymbol(piece));
            }
            System.out.println(" " + rank);
        }

        // Print column labels again
        System.out.print("   ");
        for (String file : files) {
            System.out.print(file + " \u2003");
        }
        System.out.println();
    }
    public static void drawBoardWithHighlights(ChessBoard board, Collection<ChessMove> highlightedMoves) {
        // Store highlighted positions for quick lookup
        Set<ChessPosition> highlightedPositions = new HashSet<>();
        for (ChessMove move : highlightedMoves) {
            highlightedPositions.add(move.getEndPosition());
        }

        // Draw the board
        for (int row = 8; row >= 1; row--) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                boolean isHighlighted = highlightedPositions.contains(position);
                String highlightCode = isHighlighted ? "\u001B[43m" : ""; // Yellow background for highlighted squares
                String resetCode = "\u001B[0m";

                System.out.print(highlightCode + getPieceSymbol(piece) + resetCode);
            }
            System.out.println();
        }
    }

    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return " \u2003 " + ANSI_RESET;
        }
        String color = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? ANSI_WHITE_PIECE : ANSI_BLACK_PIECE;

        switch (piece.getPieceType()) {
            case KING: return color + " ♚ " + ANSI_RESET;
            case QUEEN: return color + " ♛ " + ANSI_RESET;
            case ROOK: return color + " ♜ " + ANSI_RESET;
            case BISHOP: return color + " ♝ " + ANSI_RESET;
            case KNIGHT: return color + " ♞ " + ANSI_RESET;
            case PAWN: return color + " ♟ " + ANSI_RESET;
            default: return " ";
        }
    }
}
