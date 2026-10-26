package hu.nye.progtech.rushhour;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.BoardBuilder;
import hu.nye.progtech.rushhour.model.Direction;

/** A tesztekben használt kis pályák. */
public final class TestBoards {

    /** A {@link #simple()} pálya szöveges leírása. */
    public static final String SIMPLE_TEXT = String.join("\n",
            "4 6",
            "A RIGHT",
            "B LEFT",
            "C.....",
            "CAA...",
            "....BB",
            "......") + "\n";

    private TestBoards() {
        // csak statikus metódusok
    }

    /**
     * 4x6-os pálya. A játékos A járműve az 1. sorban jobbra, a gép B járműve a 2. sorban balra juthat ki.
     * <pre>
     * C . . . . .
     * C A A . . .
     * . . . . B B
     * . . . . . .
     * </pre>
     */
    public static Board simple() {
        return new BoardBuilder(4, 6)
                .humanExit(Direction.RIGHT)
                .machineExit(Direction.LEFT)
                .vertical('C', 0, 0, 2)
                .horizontal('A', 1, 1, 2)
                .horizontal('B', 2, 4, 2)
                .build();
    }

    /** 5x5-ös pálya függőleges céljárművekkel: A lefelé, B felfelé juthat ki. */
    public static Board verticalExits() {
        return new BoardBuilder(5, 5)
                .humanExit(Direction.DOWN)
                .machineExit(Direction.UP)
                .vertical('A', 1, 1, 2)
                .vertical('B', 2, 3, 2)
                .build();
    }

    /** Olyan pálya, amelyen az A jármű már a kijáraton áll. */
    public static Board solved() {
        return new BoardBuilder(2, 4)
                .humanExit(Direction.RIGHT)
                .machineExit(Direction.LEFT)
                .horizontal('A', 0, 2, 2)
                .horizontal('B', 1, 1, 2)
                .build();
    }

    /** Teljesen tele pálya, amelyen egyik jármű sem tud lépni. */
    public static Board deadlocked() {
        return new BoardBuilder(2, 4)
                .humanExit(Direction.RIGHT)
                .machineExit(Direction.LEFT)
                .horizontal('A', 0, 0, 2)
                .horizontal('B', 0, 2, 2)
                .horizontal('C', 1, 0, 2)
                .horizontal('D', 1, 2, 2)
                .build();
    }
}
