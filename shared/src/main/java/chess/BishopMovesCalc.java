package chess;
import java.util.Collection;
public class BishopMovesCalc {
    public static void pieceMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves){
        MovesCalculator.movX(board,position,validMoves,1,1,true);
        MovesCalculator.movX(board,position,validMoves,1,-1,true);
        MovesCalculator.movX(board,position,validMoves,-1,-1,true);
        MovesCalculator.movX(board,position,validMoves,-1,1,true);
    }
}
