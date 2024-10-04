package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.*;
 public class PawnMovesCalc {
    public static void pieceMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> validMoves){
        ChessPiece piece = board.getPiece(position);
        int dir = (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK ? -1:1);
        Collection<ChessMove> potentialMoves = new ArrayList<>();
        if (board.getPiece(new ChessPosition(position.getRow()+dir, position.getColumn())) == null) {
            MovesCalculator.movX(board, position, potentialMoves, new int[][]{{dir, 0}}, false);
            if ((position.getRow() == 2 || position.getRow() == 7) &&
            board.getPiece(new ChessPosition(position.getRow()+2*dir, position.getColumn())) == null)
                     {
                MovesCalculator.movX(board, position, potentialMoves, new int[][]{{2 * dir, 0}}, false);
            }
        }

        /* Attack Diagonally */
        if (board.getPiece(new ChessPosition(position.getRow()+dir, position.getColumn()+1)) != null) {
            MovesCalculator.movX(board, position, potentialMoves, new int[][]{{dir, 1}}, false);
        }
        if (board.getPiece(new ChessPosition(position.getRow()+dir, position.getColumn()-1)) != null) {
            MovesCalculator.movX(board, position, potentialMoves, new int[][]{{dir, -1}}, false);
        }
        /* Promote */
        for( ChessMove potentialMove : potentialMoves){
            if (potentialMove.getEndPosition().getRow()==1 || potentialMove.getEndPosition().getRow()==8){
                addPromotions(board, validMoves, potentialMove);
            }
            else {
                validMoves.add(potentialMove);
            }
        }

    }
    public static void addPromotions(ChessBoard board, Collection<ChessMove> validMoves, ChessMove promoMove){
        ChessPiece.PieceType[] promoTypes = new ChessPiece.PieceType[]{QUEEN, ROOK, KNIGHT, BISHOP};
        for(ChessPiece.PieceType promoType : promoTypes) {
            validMoves.add(new ChessMove(promoMove.getStartPosition(), promoMove.getEndPosition(), promoType));
        }
    }

}
