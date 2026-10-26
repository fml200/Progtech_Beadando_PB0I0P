package hu.nye.progtech.rushhour.model;

import java.util.Locale;

/** Csúsztatási irány. A sor 0 a tábla teteje, az oszlop 0 a bal széle. */
public enum Direction {
    UP("fel", -1, 0, Orientation.VERTICAL),
    DOWN("le", 1, 0, Orientation.VERTICAL),
    LEFT("balra", 0, -1, Orientation.HORIZONTAL),
    RIGHT("jobbra", 0, 1, Orientation.HORIZONTAL);

    private final String label;
    private final int rowDelta;
    private final int columnDelta;
    private final Orientation orientation;

    Direction(String label, int rowDelta, int columnDelta, Orientation orientation) {
        this.label = label;
        this.rowDelta = rowDelta;
        this.columnDelta = columnDelta;
        this.orientation = orientation;
    }

    public String getLabel() {
        return label;
    }

    public int getRowDelta() {
        return rowDelta;
    }

    public int getColumnDelta() {
        return columnDelta;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    /** Igaz, ha az irány a növekvő sor- vagy oszlopindex felé mutat (le, jobbra). */
    public boolean isForward() {
        return rowDelta + columnDelta > 0;
    }

    /**
     * Irány felismerése angol (up, down, left, right) vagy magyar (fel, le, balra, jobbra) szóból.
     *
     * @throws IllegalArgumentException ha a szöveg nem irány
     */
    public static Direction fromText(String text) {
        String normalized = text.trim().toLowerCase(Locale.ROOT);
        for (Direction direction : values()) {
            if (direction.name().toLowerCase(Locale.ROOT).equals(normalized) || direction.label.equals(normalized)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Ismeretlen irány: " + text);
    }
}
