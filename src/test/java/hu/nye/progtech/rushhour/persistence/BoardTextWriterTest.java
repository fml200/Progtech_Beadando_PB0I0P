package hu.nye.progtech.rushhour.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import hu.nye.progtech.rushhour.TestBoards;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Move;
import hu.nye.progtech.rushhour.service.DefaultBoards;

class BoardTextWriterTest {

    private final BoardTextWriter writer = new BoardTextWriter();
    private final BoardTextReader reader = new BoardTextReader();

    @Test
    void writesSizeExitsAndGrid() {
        String expected = "# Rush Hour pálya\n" + TestBoards.SIMPLE_TEXT;

        assertEquals(expected, writer.write(TestBoards.simple()));
    }

    @Test
    void writtenTextCanBeReadBack() {
        Board midGame = TestBoards.simple().apply(new Move('C', Direction.DOWN, 2));

        assertEquals(midGame, reader.parse(writer.write(midGame)));
    }

    @Test
    void defaultBoardSurvivesARoundTrip() {
        Board standard = DefaultBoards.standard();

        assertEquals(standard, reader.parse(writer.write(standard)));
    }

    @Test
    void verticalExitsAreWrittenByName() {
        String text = writer.write(TestBoards.verticalExits());

        assertTrue(text.contains("A DOWN\nB UP\n"));
    }
}
