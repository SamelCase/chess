package chess;
import java.util.Collection;
public class KingMovesCalc {
    public static void pieceMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves){
        int[][] kingMoves = { {1,1}, {1,-1}, {-1,1}, {-1,-1}, {0,1},{0,-1},{-1,0},{1,0} };
        MovesCalculator.movX(board,position,validMoves,kingMoves,false);
    }
}
