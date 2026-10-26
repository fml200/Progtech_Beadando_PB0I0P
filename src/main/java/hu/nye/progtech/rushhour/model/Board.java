package hu.nye.progtech.rushhour.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import hu.nye.progtech.rushhour.exception.InvalidBoardException;
import hu.nye.progtech.rushhour.exception.InvalidMoveException;

/**
 * A játéktábla: N sor, M oszlop, járművek és a két céljármű kijárata. Változtathatatlan:
 * egy lépés ({@link #apply(Move)}) új táblát ad vissza.
 * A kijárat a tábla szélének az a mezője, amely a céljármű sorában (vagy oszlopában) van
 * a megadott irányban. A játékos akkor nyer, ha a céljárműve eléri ezt a mezőt.
 */
public final class Board {

    public static final int MAX_SIZE = 26;

    private static final char EMPTY = '.';

    private final int rows;
    private final int columns;
    private final Map<Character, Vehicle> vehicles;
    private final Map<Side, Direction> exits;
    private final char[][] grid;

    /**
     * Létrehoz egy táblát.
     *
     * @throws InvalidBoardException ha a pálya érvénytelen (méret, átfedés, hiányzó céljármű, rossz kijárat)
     */
    public Board(int rows, int columns, Collection<Vehicle> vehicles, Map<Side, Direction> exits) {
        validateSize(rows, columns);
        Objects.requireNonNull(vehicles, "vehicles");
        Objects.requireNonNull(exits, "exits");
        this.rows = rows;
        this.columns = columns;
        this.vehicles = copyVehicles(vehicles);
        this.grid = createGrid();
        this.exits = copyExits(exits);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    /** A járművek azonosító szerint rendezve; a gyűjtemény nem módosítható. */
    public Collection<Vehicle> getVehicles() {
        return vehicles.values();
    }

    public Optional<Vehicle> getVehicle(char id) {
        return Optional.ofNullable(vehicles.get(id));
    }

    public Direction getExit(Side side) {
        return exits.get(side);
    }

    /** A kijárat mezője: a céljármű sorának/oszlopának a tábla szélén lévő vége. */
    public Position getExitPosition(Side side) {
        Vehicle hero = vehicles.get(side.getHeroId());
        Direction exit = exits.get(side);
        if (exit == Direction.UP) {
            return new Position(0, hero.start().column());
        }
        if (exit == Direction.DOWN) {
            return new Position(rows - 1, hero.start().column());
        }
        if (exit == Direction.LEFT) {
            return new Position(hero.start().row(), 0);
        }
        return new Position(hero.start().row(), columns - 1);
    }

    public boolean isInside(Position position) {
        return position.row() >= 0 && position.row() < rows
                && position.column() >= 0 && position.column() < columns;
    }

    public boolean isFree(Position position) {
        return isInside(position) && grid[position.row()][position.column()] == EMPTY;
    }

    public Optional<Vehicle> getVehicleAt(Position position) {
        if (!isInside(position)) {
            return Optional.empty();
        }
        char id = grid[position.row()][position.column()];
        return id == EMPTY ? Optional.empty() : Optional.of(vehicles.get(id));
    }

    public boolean hasReachedExit(Side side) {
        return vehicles.get(side.getHeroId()).occupies(getExitPosition(side));
    }

    /** Az a játékos, akinek a céljárműve elérte a kijáratot (ha van ilyen). */
    public Optional<Side> findWinner() {
        for (Side side : Side.values()) {
            if (hasReachedExit(side)) {
                return Optional.of(side);
            }
        }
        return Optional.empty();
    }

    /**
     * Hány egybefüggő üres mezőt csúszhat a jármű az adott irányba.
     *
     * @throws InvalidMoveException ha nincs ilyen jármű, vagy az irány nem a tengelyébe esik
     */
    public int getMaxSlide(char vehicleId, Direction direction) {
        Vehicle vehicle = requireVehicle(vehicleId);
        requireAxis(vehicle, direction);
        return countFreeCells(vehicle, direction);
    }

    /** Az összes szabályos lépés. */
    public List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        for (Vehicle vehicle : vehicles.values()) {
            moves.addAll(getPossibleMoves(vehicle.id()));
        }
        return moves;
    }

    /**
     * Az adott jármű összes szabályos lépése (iránytól és lépésszámtól függően).
     *
     * @throws InvalidMoveException ha nincs ilyen jármű
     */
    public List<Move> getPossibleMoves(char vehicleId) {
        Vehicle vehicle = requireVehicle(vehicleId);
        List<Move> moves = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (direction.getOrientation() == vehicle.orientation()) {
                int max = countFreeCells(vehicle, direction);
                for (int steps = 1; steps <= max; steps++) {
                    moves.add(new Move(vehicleId, direction, steps));
                }
            }
        }
        return moves;
    }

    /**
     * Végrehajtja a lépést, és az új táblát adja vissza; ez a tábla nem változik.
     *
     * @throws InvalidMoveException ha a lépés szabálytalan
     */
    public Board apply(Move move) {
        Vehicle vehicle = requireVehicle(move.vehicleId());
        Direction direction = move.direction();
        requireAxis(vehicle, direction);
        if (move.steps() < 1) {
            throw new InvalidMoveException("A lépésszámnak legalább 1-nek kell lennie.");
        }
        int max = countFreeCells(vehicle, direction);
        if (max == 0) {
            throw new InvalidMoveException("Az " + vehicle.id() + " jármű nem mozdulhat "
                    + direction.getLabel() + " irányba.");
        }
        if (move.steps() > max) {
            throw new InvalidMoveException("Az " + vehicle.id() + " jármű legfeljebb " + max
                    + " mezőt csúszhat " + direction.getLabel() + " irányba.");
        }

        List<Vehicle> updated = new ArrayList<>();
        for (Vehicle current : vehicles.values()) {
            updated.add(current.id() == vehicle.id() ? vehicle.moved(direction, move.steps()) : current);
        }
        return new Board(rows, columns, updated, exits);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Board board = (Board) other;
        return rows == board.rows && columns == board.columns
                && vehicles.equals(board.vehicles) && exits.equals(board.exits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rows, columns, vehicles, exits);
    }

    @Override
    public String toString() {
        return "Board{rows=" + rows + ", columns=" + columns + ", vehicles=" + vehicles.values()
                + ", exits=" + exits + '}';
    }

    private int countFreeCells(Vehicle vehicle, Direction direction) {
        Position front = direction.isForward() ? vehicle.end() : vehicle.start();
        int count = 0;
        Position next = front.shifted(direction, 1);
        while (isFree(next)) {
            count++;
            next = next.shifted(direction, 1);
        }
        return count;
    }

    private Vehicle requireVehicle(char vehicleId) {
        Vehicle vehicle = vehicles.get(vehicleId);
        if (vehicle == null) {
            throw new InvalidMoveException("Nincs ilyen jármű: " + vehicleId);
        }
        return vehicle;
    }

    private void requireAxis(Vehicle vehicle, Direction direction) {
        if (direction.getOrientation() != vehicle.orientation()) {
            throw new InvalidMoveException("Az " + vehicle.id() + " jármű csak "
                    + vehicle.orientation().getLabel() + " mozoghat.");
        }
    }

    private static void validateSize(int rows, int columns) {
        if (rows < 1 || rows > MAX_SIZE || columns < 1 || columns > MAX_SIZE) {
            throw new InvalidBoardException("A tábla sorainak és oszlopainak száma 1 és "
                    + MAX_SIZE + " között lehet.");
        }
    }

    private static Map<Character, Vehicle> copyVehicles(Collection<Vehicle> source) {
        Map<Character, Vehicle> copy = new TreeMap<>();
        for (Vehicle vehicle : source) {
            if (copy.put(vehicle.id(), vehicle) != null) {
                throw new InvalidBoardException("Az " + vehicle.id() + " azonosítóból több jármű van.");
            }
        }
        return Collections.unmodifiableMap(copy);
    }

    private char[][] createGrid() {
        char[][] cells = new char[rows][columns];
        for (char[] row : cells) {
            Arrays.fill(row, EMPTY);
        }
        for (Vehicle vehicle : vehicles.values()) {
            for (Position position : vehicle.cells()) {
                if (!isInside(position)) {
                    throw new InvalidBoardException("Az " + vehicle.id() + " jármű kilóg a pályáról.");
                }
                if (cells[position.row()][position.column()] != EMPTY) {
                    throw new InvalidBoardException("Az " + vehicle.id() + " jármű átfedi egy másik járművet.");
                }
                cells[position.row()][position.column()] = vehicle.id();
            }
        }
        return cells;
    }

    private Map<Side, Direction> copyExits(Map<Side, Direction> source) {
        Map<Side, Direction> copy = new EnumMap<>(Side.class);
        for (Side side : Side.values()) {
            Vehicle hero = vehicles.get(side.getHeroId());
            if (hero == null) {
                throw new InvalidBoardException("Hiányzik a(z) " + side.getHeroId() + " céljármű.");
            }
            Direction exit = source.get(side);
            if (exit == null) {
                throw new InvalidBoardException("Hiányzik a(z) " + side.getHeroId() + " jármű kijárata.");
            }
            if (exit.getOrientation() != hero.orientation()) {
                throw new InvalidBoardException("A(z) " + side.getHeroId() + " jármű kijárata ("
                        + exit + ") nem esik a jármű tengelyébe.");
            }
            copy.put(side, exit);
        }
        return Collections.unmodifiableMap(copy);
    }
}
