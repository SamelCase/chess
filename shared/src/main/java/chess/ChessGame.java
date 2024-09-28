package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;
    public ChessGame() {
        this.board = new ChessBoard();
        this.turn = TeamColor.WHITE;
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return board.getPiece(startPosition).pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        board.addPiece(move.getStartPosition(), null);
        if (board.getPiece(move.getEndPosition()).getPieceType() == KING) {
            board.updateKingPos(move.getEndPosition());
        }
        setTeamTurn(this.turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
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

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        KingMovesCalc.pieceMoves(board,board.getKingPos(teamColor),validMoves);
        return isInCheck(teamColor) && (validMoves.isEmpty());
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
