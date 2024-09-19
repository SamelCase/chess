package chess;

import java.util.Collection;

public class RookMovesCalc {
    public static void pieceMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves) {
        int[][] rookMoves = { {0,1},{1,0},{-1,0},{1,0} };
        MovesCalculator.movX(board,position,validMoves,rookMoves,true);
    }
}
