package hu.nye.progtech.rushhour.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import hu.nye.progtech.rushhour.TestBoards;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.BoardBuilder;
import hu.nye.progtech.rushhour.model.Direction;

class BoardRendererTest {

    private final BoardRenderer renderer = new BoardRenderer();

    @Test
    void rendersColumnLettersRowNumbersAndHorizontalExits() {
        String expected = String.join("\n",
                "    a b c d e f",
                "  +-------------+",
                "1 | C . . . . . |",
                "2 | C A A . . . >",
                "3 < . . . . B B |",
                "4 | . . . . . . |",
                "  +-------------+",
                "A: Játékos, kijárat: jobbra",
                "B: Gép, kijárat: balra");

        assertEquals(expected, renderer.render(TestBoards.simple()));
    }

    @Test
    void rendersVerticalExitsInTheTopAndBottomBorder() {
        String expected = String.join("\n",
                "    a b c d e",
                "  +-------^---+",
                "1 | . . . . . |",
                "2 | . A . . . |",
                "3 | . A . B . |",
                "4 | . . . B . |",
                "5 | . . . . . |",
                "  +---v-------+",
                "A: Játékos, kijárat: le",
                "B: Gép, kijárat: fel");

        assertEquals(expected, renderer.render(TestBoards.verticalExits()));
    }

    @Test
    void widensTheRowLabelForTenOrMoreRows() {
        Board tall = new BoardBuilder(10, 3)
                .humanExit(Direction.RIGHT)
                .machineExit(Direction.LEFT)
                .horizontal('A', 0, 0, 2)
                .horizontal('B', 9, 1, 2)
                .build();

        List<String> lines = renderer.render(tall).lines().toList();

        assertEquals("     a b c", lines.get(0));
        assertTrue(lines.get(2).startsWith(" 1 | A A ."), lines.get(2));
        assertTrue(lines.get(11).startsWith("10 < . B B"), lines.get(11));
    }
}
