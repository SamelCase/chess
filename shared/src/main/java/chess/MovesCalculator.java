package chess;

import java.util.ArrayList;
import java.util.Collection;
public class MovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        Collection<ChessMove> validMoves = new ArrayList<>();

        return validMoves;
    }
}
