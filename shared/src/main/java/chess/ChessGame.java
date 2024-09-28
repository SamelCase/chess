package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessPiece.PieceType.*;
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;
    public ChessGame() {
        this.board = new ChessBoard();
        this.turn = TeamColor.WHITE;
        board.resetBoard();
    }
    public TeamColor getTeamTurn() {
        return turn;
    }
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return board.getPiece(startPosition).pieceMoves(board, startPosition);
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        board.addPiece(move.getStartPosition(), null);
        if (board.getPiece(move.getEndPosition()).getPieceType() == KING) {
            board.updateKingPos(move.getEndPosition());
        }
        setTeamTurn(this.turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> attackVectors = new ArrayList<>();
        ChessPosition kingPos = board.getKingPos(turn);
        /* Calculate from where a bishop could attack from; check if a bishop is at that position.
        clear list and Repeat for other pieces */
        BishopMovesCalc.pieceMoves(board,kingPos,attackVectors);
        for (ChessMove move:attackVectors) {
            if (board.getPiece(move.getEndPosition()).getPieceType()==BISHOP
            || board.getPiece(move.getEndPosition()).getPieceType()==QUEEN) return true;
        }
        attackVectors.clear();
        RookMovesCalc.pieceMoves(board,kingPos,attackVectors);
        for (ChessMove move:attackVectors) {
            if (board.getPiece(move.getEndPosition()).getPieceType()==ROOK
                    || board.getPiece(move.getEndPosition()).getPieceType()==QUEEN) return true;
        }
        attackVectors.clear();
        KnightMovesCalc.pieceMoves(board,kingPos,attackVectors);
        for (ChessMove move:attackVectors) {
            if (board.getPiece(move.getEndPosition()).getPieceType()==KNIGHT) return true;
        }
        attackVectors.clear();
        PawnMovesCalc.pieceMoves(board,kingPos,attackVectors);
        for (ChessMove move:attackVectors) {
            if (board.getPiece(move.getEndPosition()).getPieceType()==PAWN) return true;
        }
        attackVectors.clear();
        return false;
    }
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        KingMovesCalc.pieceMoves(board,board.getKingPos(teamColor),validMoves);
        return isInCheck(teamColor) && !hasLegalMoves(teamColor);
    }
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        KingMovesCalc.pieceMoves(board,board.getKingPos(teamColor),validMoves);
        return !isInCheck(teamColor) && !hasLegalMoves(teamColor);
    }
    private boolean hasLegalMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> legalMoves = piece.pieceMoves(board,position);
                    if (!legalMoves.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void setBoard(ChessBoard board) {
        this.board = board;
    }
    public ChessBoard getBoard() {
        return board;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }
    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
