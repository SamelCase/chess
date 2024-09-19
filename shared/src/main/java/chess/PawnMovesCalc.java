package chess;

import java.util.Collection;

public class PawnMovesCalc {
    public static void pieceMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves){
        int[][] pawnMoves = { {1,1}, {1,-1}, {-1,1}, {-1,-1}, {1,0}, {-1,0}};
        MovesCalculator.movX(board,position,validMoves,pawnMoves,true);
    }
}
