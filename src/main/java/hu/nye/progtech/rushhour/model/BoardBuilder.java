package hu.nye.progtech.rushhour.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Builder minta: lépésenként, olvashatóan összeállítható pálya. */
public final class BoardBuilder {

    private final int rows;
    private final int columns;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final Map<Side, Direction> exits = new EnumMap<>(Side.class);

    public BoardBuilder(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public BoardBuilder humanExit(Direction direction) {
        exits.put(Side.HUMAN, direction);
        return this;
    }

    public BoardBuilder machineExit(Direction direction) {
        exits.put(Side.MACHINE, direction);
        return this;
    }

    public BoardBuilder horizontal(char id, int row, int column, int length) {
        vehicles.add(new Vehicle(id, new Position(row, column), length, Orientation.HORIZONTAL));
        return this;
    }

    public BoardBuilder vertical(char id, int row, int column, int length) {
        vehicles.add(new Vehicle(id, new Position(row, column), length, Orientation.VERTICAL));
        return this;
    }

    /**
     * Összeállítja a táblát.
     *
     * @throws hu.nye.progtech.rushhour.exception.InvalidBoardException ha a pálya érvénytelen
     */
    public Board build() {
        return new Board(rows, columns, vehicles, exits);
    }
}
