package chess;

import java.util.Collection;

public class RookMovesCalc {
    public static void pieceMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves) {
        MovesCalculator.movX(board, position, validMoves, 0, 1, true);
        MovesCalculator.movX(board, position, validMoves, 0, -1, true);
        MovesCalculator.movX(board, position, validMoves, 1, 0, true);
        MovesCalculator.movX(board, position, validMoves, -1, 0, true);
    }
}
