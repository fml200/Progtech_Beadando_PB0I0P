package hu.nye.progtech.rushhour.ui;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Side;

/**
 * A táblát szöveggé alakítja. Az oszlopokat betűk (a, b, c, ...), a sorokat számok (1, 2, 3, ...)
 * jelölik; a kijáratokat a keret megszakítása mutatja (&gt;, &lt;, ^, v).
 */
public class BoardRenderer {

    private static final char EMPTY = '.';

    public String render(Board board) {
        int labelWidth = String.valueOf(board.getRows()).length();
        StringBuilder text = new StringBuilder();

        text.append(" ".repeat(labelWidth + 3));
        for (int column = 0; column < board.getColumns(); column++) {
            if (column > 0) {
                text.append(' ');
            }
            text.append((char) ('a' + column));
        }
        text.append('\n');

        text.append(horizontalBorder(board, labelWidth, Direction.UP, '^')).append('\n');
        for (int row = 0; row < board.getRows(); row++) {
            text.append(String.format("%" + labelWidth + "d", row + 1)).append(' ');
            text.append(sideBorder(board, row, Direction.LEFT, '<')).append(' ');
            for (int column = 0; column < board.getColumns(); column++) {
                if (column > 0) {
                    text.append(' ');
                }
                text.append(board.getVehicleAt(new Position(row, column)).map(v -> v.id()).orElse(EMPTY));
            }
            text.append(' ').append(sideBorder(board, row, Direction.RIGHT, '>')).append('\n');
        }
        text.append(horizontalBorder(board, labelWidth, Direction.DOWN, 'v')).append('\n');

        for (Side side : Side.values()) {
            text.append(side.getHeroId()).append(": ").append(side.getLabel())
                    .append(", kijárat: ").append(board.getExit(side).getLabel()).append('\n');
        }
        return text.toString().stripTrailing();
    }

    private char sideBorder(Board board, int row, Direction exitDirection, char marker) {
        for (Side side : Side.values()) {
            if (board.getExit(side) == exitDirection && board.getExitPosition(side).row() == row) {
                return marker;
            }
        }
        return '|';
    }

    private String horizontalBorder(Board board, int labelWidth, Direction exitDirection, char marker) {
        char[] border = (" ".repeat(labelWidth + 1) + "+" + "-".repeat(2 * board.getColumns() + 1) + "+")
                .toCharArray();
        for (Side side : Side.values()) {
            if (board.getExit(side) == exitDirection) {
                border[labelWidth + 3 + 2 * board.getExitPosition(side).column()] = marker;
            }
        }
        return new String(border);
    }
}
