package hu.nye.progtech.rushhour.persistence;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.nye.progtech.rushhour.exception.InvalidBoardException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Side;
import hu.nye.progtech.rushhour.model.Vehicle;

/**
 * Szöveges pályaleírásból {@link Board}-ot készít. A formátum (a # kezdetű és az üres sorok
 * kimaradnak):
 * <pre>
 * 6 12          sorok és oszlopok száma
 * A RIGHT       az A (játékos) céljármű kijárata: UP, DOWN, LEFT vagy RIGHT
 * B LEFT        a B (gép) céljármű kijárata
 * ............  a rács: '.' üres mező, A-Z a jármű azonosítója
 * </pre>
 */
public class BoardTextReader {

    private static final Pattern SIZE_PATTERN = Pattern.compile("^(\\d+)\\s+(\\d+)$");
    private static final Pattern EXIT_PATTERN =
            Pattern.compile("^([AB])\\s+(UP|DOWN|LEFT|RIGHT)$", Pattern.CASE_INSENSITIVE);
    private static final char EMPTY = '.';
    private static final int HEADER_LINES = 3;

    /**
     * Feldolgozza a pályaleírást.
     *
     * @throws InvalidBoardException ha a szöveg nem érvényes pálya
     */
    public Board parse(String text) {
        List<String> lines = significantLines(text);
        if (lines.size() < HEADER_LINES) {
            throw new InvalidBoardException("A pályaleírás hiányos: méret, két kijárat és rács szükséges.");
        }
        int[] size = parseSize(lines.get(0));
        int rows = size[0];
        int columns = size[1];
        Map<Side, Direction> exits = parseExits(lines.get(1), lines.get(2));

        List<String> gridLines = lines.subList(HEADER_LINES, lines.size());
        if (gridLines.size() != rows) {
            throw new InvalidBoardException("A rácsnak " + rows + " sora kell legyen, de " + gridLines.size()
                    + " van.");
        }
        Board board = new Board(rows, columns, parseVehicles(gridLines, columns), exits);
        if (board.findWinner().isPresent()) {
            throw new InvalidBoardException("A pálya már a kezdőállapotban megoldott.");
        }
        return board;
    }

    private List<String> significantLines(String text) {
        List<String> lines = new ArrayList<>();
        for (String line : text.split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                lines.add(trimmed);
            }
        }
        return lines;
    }

    private int[] parseSize(String line) {
        Matcher matcher = SIZE_PATTERN.matcher(line);
        if (!matcher.matches()) {
            throw new InvalidBoardException("Hibás méret sor: '" + line + "' (várt formátum: sorok oszlopok).");
        }
        try {
            return new int[] {Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))};
        } catch (NumberFormatException e) {
            throw new InvalidBoardException("Túl nagy méret: '" + line + "'.");
        }
    }

    private Map<Side, Direction> parseExits(String first, String second) {
        Map<Side, Direction> exits = new EnumMap<>(Side.class);
        for (String line : List.of(first, second)) {
            Matcher matcher = EXIT_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new InvalidBoardException("Hibás kijárat sor: '" + line + "' (várt formátum: A RIGHT).");
            }
            Side side = matcher.group(1).equalsIgnoreCase("A") ? Side.HUMAN : Side.MACHINE;
            Direction direction = Direction.valueOf(matcher.group(2).toUpperCase(Locale.ROOT));
            if (exits.put(side, direction) != null) {
                throw new InvalidBoardException("A(z) " + side.getHeroId() + " kijárata kétszer szerepel.");
            }
        }
        return exits;
    }

    private List<Vehicle> parseVehicles(List<String> gridLines, int columns) {
        Map<Character, List<Position>> cellsById = new TreeMap<>();
        for (int row = 0; row < gridLines.size(); row++) {
            String line = gridLines.get(row);
            if (line.length() != columns) {
                throw new InvalidBoardException("A(z) " + (row + 1) + ". rácssor hossza " + line.length()
                        + ", de " + columns + " kell.");
            }
            for (int column = 0; column < columns; column++) {
                char symbol = line.charAt(column);
                if (symbol == EMPTY) {
                    continue;
                }
                if (symbol < 'A' || symbol > 'Z') {
                    throw new InvalidBoardException("Érvénytelen karakter a rácsban: '" + symbol
                            + "' (" + (row + 1) + ". sor).");
                }
                cellsById.computeIfAbsent(symbol, key -> new ArrayList<>()).add(new Position(row, column));
            }
        }

        List<Vehicle> vehicles = new ArrayList<>();
        for (Map.Entry<Character, List<Position>> entry : cellsById.entrySet()) {
            vehicles.add(toVehicle(entry.getKey(), entry.getValue()));
        }
        return vehicles;
    }

    private Vehicle toVehicle(char id, List<Position> cells) {
        if (cells.size() < Vehicle.MIN_LENGTH) {
            throw new InvalidBoardException("Az " + id + " jármű csak 1 mezőből áll.");
        }
        Position first = cells.get(0);
        Position last = cells.get(cells.size() - 1);
        Orientation orientation;
        if (first.row() == last.row()) {
            orientation = Orientation.HORIZONTAL;
        } else if (first.column() == last.column()) {
            orientation = Orientation.VERTICAL;
        } else {
            throw new InvalidBoardException("Az " + id + " jármű mezői nem egy sorban vagy oszlopban vannak.");
        }
        Vehicle vehicle = new Vehicle(id, first, cells.size(), orientation);
        if (!vehicle.cells().equals(cells)) {
            throw new InvalidBoardException("Az " + id + " jármű mezői nem összefüggőek.");
        }
        return vehicle;
    }
}
