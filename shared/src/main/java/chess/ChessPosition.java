package chess;
import java.util.Objects;
/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private final int row;
    private final int col;
    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public int getRow() {
        return row;
    }
    public int getColumn() {
        return col;
    }
    public static ChessPosition fromAlgebraic(String algebraicPosition) {
        // Alternate 'constructor' for when we want to create a chess position alphanumerically, which will be practical.
        if (algebraicPosition.length() != 2) {
            throw new IllegalArgumentException("Invalid algebraic position format");
        }
        char fileChar = algebraicPosition.charAt(0);
        char rankChar = algebraicPosition.charAt(1);

        int col = fileChar - 'a' + 1;
        int row = rankChar - '0';

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Invalid algebraic position: " + algebraicPosition);
        }

        return new ChessPosition(row, col);
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + row +
                ", col=" + col +
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
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}












