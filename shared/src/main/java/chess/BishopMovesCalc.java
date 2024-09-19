package chess;
import java.util.Collection;
public class BishopMovesCalc {
    public static void pieceMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves){
        int[][] bishopMoves = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1} };
        MovesCalculator.movX(board,position,validMoves,bishopMoves,true);
    }
}
