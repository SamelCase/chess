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
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move:board.getPiece(startPosition).pieceMoves(board, startPosition)) {
            try {
                if (!wouldResultInCheck(move)
                ) {
                    validMoves.add(move);
                }
            }
            catch (InvalidMoveException e){
                    continue;
                }
        }
        return validMoves;
    }
    private void doMove(ChessMove move, ChessBoard board) {
        if (move.getPromotionPiece()!=null){
            board.addPiece(move.getEndPosition(), new ChessPiece(board.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece()));
        }
        else {
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        }
        board.addPiece(move.getStartPosition(), null);
        if (board.getPiece(move.getEndPosition()).getPieceType() == KING) {
            board.updateKingPos(move.getEndPosition());
        }
    }
    private boolean wouldResultInCheck(ChessMove move) throws InvalidMoveException {
        ChessBoard simBoard = (ChessBoard) board.clone();
        doMove(move, simBoard);
        return isBoardInCheck(simBoard, simBoard.getPiece(move.getEndPosition()).getTeamColor());
    }
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) == null) throw new InvalidMoveException();
        if (board.getPiece(move.getStartPosition()).getTeamColor() != turn) throw new InvalidMoveException();
        if (!(validMoves(move.getStartPosition()).contains(move))) throw new InvalidMoveException();
        doMove(move, board);
        setTeamTurn(this.turn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
    }
    private boolean isBoardInCheck (ChessBoard board, TeamColor teamColor) {
        Collection<ChessMove> attackVectors = new ArrayList<>();
        ChessPosition kingPos = board.getKingPos(teamColor);
        if (kingPos == null) return false;
        /* Calculate from where a bishop could attack from; check if a bishop is at that position.
        clear list and Repeat for other pieces */
        BishopMovesCalc.pieceMoves(board,kingPos,attackVectors);
        for (ChessMove move:attackVectors) {
            ChessPiece piece = board.getPiece(move.getEndPosition());
            if (!(piece==null) && (piece.getPieceType()==BISHOP
                    || board.getPiece(move.getEndPosition()).getPieceType()==QUEEN)) return true;
        }
        attackVectors.clear();
        RookMovesCalc.pieceMoves(board,kingPos,attackVectors);
        for (ChessMove move:attackVectors) {
            ChessPiece piece = board.getPiece(move.getEndPosition());
            if (!(piece==null) && (piece.getPieceType()==ROOK
                    || board.getPiece(move.getEndPosition()).getPieceType()==QUEEN)) return true;
        }
        attackVectors.clear();
        KnightMovesCalc.pieceMoves(board,kingPos,attackVectors);
        for (ChessMove move:attackVectors) {
            ChessPiece piece = board.getPiece(move.getEndPosition());
            if (!(piece==null) && (piece.getPieceType()==KNIGHT)) return true;
        }
        attackVectors.clear();
        PawnMovesCalc.pieceMoves(board,kingPos,attackVectors);
        for (ChessMove move:attackVectors) {
            ChessPiece piece = board.getPiece(move.getEndPosition());
            if (!(piece==null) && (piece.getPieceType()==PAWN)) return true;
        }
        attackVectors.clear();
        KingMovesCalc.pieceMoves(board,kingPos,attackVectors);
        for (ChessMove move:attackVectors) {
            ChessPiece piece = board.getPiece(move.getEndPosition());
            if (!(piece==null) && (piece.getPieceType()==KING)) return true;
        }
        attackVectors.clear();
        return false;
    }
    public boolean isInCheck(TeamColor teamColor) {
        return isBoardInCheck(board, teamColor);
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
                if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() == teamColor &&
                        !validMoves(position).isEmpty()) return true;
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
