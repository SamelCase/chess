package chess;

import java.util.Collection;

public class KnightMovesCalc {
    public static void pieceMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves){
        int[][] knightMoves = { {2,1}, {2,-1}, {-1,2}, {1,2}, {-2,1},{-2,-1},{-1,-2},{1,-2} };
        MovesCalculator.movX(board,position,validMoves,knightMoves,false);
    }
}
