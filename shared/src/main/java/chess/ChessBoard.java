package chess;
import java.util.Arrays;

import static chess.ChessGame.TeamColor.WHITE;
import static chess.ChessPiece.PieceType.KING;

public class    ChessBoard implements Cloneable{
    private ChessPiece[][] squares = new ChessPiece[10][10];
    private ChessPosition whiteKingPos = null;
    private ChessPosition blackKingPos = null;
    public ChessBoard() {
    }
    public void updateKingPos(ChessPosition kingPos) {
        if (getPiece(kingPos).getTeamColor() == WHITE) {
            whiteKingPos = kingPos;
        } else {
            blackKingPos = kingPos;
        }
    }
    public ChessPosition getKingPos(ChessGame.TeamColor turn){
        return turn == WHITE ? whiteKingPos: blackKingPos;
    }
    public void addPiece(ChessPosition position, ChessPiece piece) {
    squares[position.getRow()][position.getColumn()]=piece;
        if (!(piece == null) && piece.getPieceType() == KING) {
            updateKingPos(position);
        }
    }
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()][position.getColumn()];
    }
    public void resetBoard() {
        addPiece(new ChessPosition(1, 1), new ChessPiece(WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(WHITE, ChessPiece.PieceType.ROOK));
        for (int column = 1; column < 9; column++) {
            addPiece(new ChessPosition(2, column), new ChessPiece(WHITE, ChessPiece.PieceType.PAWN));
        }
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        for (int column = 1; column < 9; column++) {
            addPiece(new ChessPosition(7, column), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }
    @Override
    protected Object clone() {
        try{
            ChessBoard board2 = (ChessBoard) super.clone();
            board2.squares = Arrays.copyOf(squares, squares.length);
            for ( int col = 0; col <10; col++){
                board2.squares[col] = Arrays.copyOf(squares[col],squares[col].length);
            }
            board2.blackKingPos = blackKingPos;
            board2.whiteKingPos = whiteKingPos;
            return board2;
        }
        catch(CloneNotSupportedException e) {
            throw new AssertionError(e);
        }

    }
    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.deepToString(squares) +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(squares, that.squares);
    }
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}













