package chess;

import java.util.Collection;

public class KnightMovesCalc {
    public static void pieceMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves){
        MovesCalculator.movX(board,position,validMoves,2,1,true);
        MovesCalculator.movX(board,position,validMoves,2,-1,true);
        MovesCalculator.movX(board,position,validMoves,-2,-1,true);
        MovesCalculator.movX(board,position,validMoves,-2,1,true);
        MovesCalculator.movX(board,position,validMoves,1,2,true);
        MovesCalculator.movX(board,position,validMoves,1,-2,true);
        MovesCalculator.movX(board,position,validMoves,-1,-2,true);
        MovesCalculator.movX(board,position,validMoves,-1,2,true);
    }
}
