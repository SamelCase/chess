package chess;
import java.util.Collection;
public class BishopMovesCalc {
    public static void pieceMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves){
        MovesCalculator.movNW(board,position,validMoves);
        MovesCalculator.movNE(board,position,validMoves);
    }
}
