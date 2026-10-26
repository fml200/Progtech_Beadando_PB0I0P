package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.BoardBuilder;
import hu.nye.progtech.rushhour.model.Direction;

/** A beégetett, alapértelmezett pálya (ha nincs érvényes pályafájl). */
public final class DefaultBoards {

    private DefaultBoards() {
        // csak statikus metódusok
    }

    /**
     * 6x12-es pálya. A játékos A járműve a 3. sorban jobbra, a gép B járműve a 4. sorban balra
     * juthat ki. A C és az E jármű mindkét céljármű útját elállja.
     *
     * <pre>
     *   a b c d e f g h i j k l
     * 1 . . . . . D . . . . F .
     * 2 . . . C . D . . . . F .
     * 3 A A . C . . . E . . . .
     * 4 . . . C . . . E . . B B
     * 5 . H . . . G . E . . . .
     * 6 . H . . . G . . J J . .
     * </pre>
     */
    public static Board standard() {
        return new BoardBuilder(6, 12)
                .humanExit(Direction.RIGHT)
                .machineExit(Direction.LEFT)
                .horizontal('A', 2, 0, 2)
                .horizontal('B', 3, 10, 2)
                .vertical('C', 1, 3, 3)
                .vertical('D', 0, 5, 2)
                .vertical('E', 2, 7, 3)
                .vertical('F', 0, 10, 2)
                .vertical('G', 4, 5, 2)
                .vertical('H', 4, 1, 2)
                .horizontal('J', 5, 8, 2)
                .build();
    }
}
