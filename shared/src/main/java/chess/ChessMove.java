package chess;
public class ChessMove {
    private final ChessPosition start;
    private final ChessPosition end;
    private final ChessPiece.PieceType promotionPiece;
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.start = startPosition;
        this.end = endPosition;
        this.promotionPiece = promotionPiece;
    }
    public ChessPosition getStartPosition() {
        return start;
    }
    public ChessPosition getEndPosition() {
        return end;
    }
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove move = (ChessMove) o;
        return (start.equals(move.start) && end.equals(move.end) && promotionPiece == move.promotionPiece);
    }
    @Override
    public int hashCode() {
        var promotionCode = (promotionPiece == null ? 9: promotionPiece.ordinal());
        return (71*start.hashCode()) + end.hashCode() + promotionCode;
    }
    @Override
    public String toString() {
        return "ChessMove{" +
                ", end=" + end +
                ", promotionPiece=" + promotionPiece +
                '}';
    }
}
