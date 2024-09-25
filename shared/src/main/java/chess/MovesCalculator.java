package chess;

import java.util.ArrayList;
import java.util.Collection;
public class MovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        Collection<ChessMove> validMoves = new ArrayList<>();

        switch(piece.getPieceType()){
            case KING -> KingMovesCalc.pieceMoves(board, position, validMoves);
            case QUEEN -> QueenMovesCalc.pieceMoves(board, position, validMoves);
            case PAWN -> PawnMovesCalc.pieceMoves(board, position, validMoves);
            case ROOK -> RookMovesCalc.pieceMoves(board, position, validMoves);
            case BISHOP -> BishopMovesCalc.pieceMoves(board, position, validMoves);
            case KNIGHT -> KnightMovesCalc.pieceMoves(board, position, validMoves);
        }
        return validMoves;
    }

    public static boolean isValidMove(ChessBoard board, ChessPosition start, ChessPosition end){
        if (end.getColumn()>8 || end.getColumn()<1 || end.getRow()>8 || end.getRow()<1 ){
            return false;
        }
        if (board.getPiece(end)!=null){
            return board.getPiece(start).getTeamColor() != board.getPiece(end).getTeamColor();
        }
        return true;
    }
    public static void addMove(ChessPosition start, ChessPosition end, Collection<ChessMove> validMoves) {
        validMoves.add(new ChessMove(start, end, null));
    }
    public static void movX(ChessBoard board, ChessPosition start, Collection<ChessMove> validMoves, int[][] directions, boolean repeatable) {
        for (int[] direction : directions) {
            int dRow = direction[0];
            int dCol = direction[1];
            int row = start.getRow();
            int col = start.getColumn();
            while (isValidMove(board, start, new ChessPosition(row+=dRow, col+=dCol))) {
                addMove(start, new ChessPosition(row, col), validMoves);
                if (board.getPiece(new ChessPosition(row, col)) != null) {
                    break;
                }
                if (!repeatable) {
                    break;
                }
            }
        }

    }

}
