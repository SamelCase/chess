package chess;

import java.util.Collection;

public class PawnMovesCalc {
    public static void pieceMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves){
        MovesCalculator.movX(board,position,validMoves,1,1,false);
        MovesCalculator.movX(board,position,validMoves,1,-1,false);
        MovesCalculator.movX(board,position,validMoves,-1,-1,false);
        MovesCalculator.movX(board,position,validMoves,-1,1,false);
        MovesCalculator.movX(board,position,validMoves,0,1,false);
        MovesCalculator.movX(board,position,validMoves,0,-1,false);
        MovesCalculator.movX(board,position,validMoves,-1,0,false);
        MovesCalculator.movX(board,position,validMoves,1,0,false);
    }
}
