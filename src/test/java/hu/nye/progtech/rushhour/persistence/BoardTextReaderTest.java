package hu.nye.progtech.rushhour.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import hu.nye.progtech.rushhour.TestBoards;
import hu.nye.progtech.rushhour.exception.InvalidBoardException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Side;

class BoardTextReaderTest {

    private final BoardTextReader reader = new BoardTextReader();

    private static String text(String... lines) {
        return String.join("\n", lines);
    }

    @Test
    void parsesAValidBoard() {
        assertEquals(TestBoards.simple(), reader.parse(TestBoards.SIMPLE_TEXT));
    }

    @Test
    void commentsBlankLinesAndWindowsLineEndingsAreIgnored() {
        String input = "# megjegyzés\r\n\r\n4 6\r\n  A RIGHT  \r\nB LEFT\r\n# rács\r\n"
                + "C.....\r\nCAA...\r\n....BB\r\n......\r\n";

        assertEquals(TestBoards.simple(), reader.parse(input));
    }

    @Test
    void exitDirectionsAreCaseInsensitive() {
        Board board = reader.parse(text("4 6", "A right", "B Left", "C.....", "CAA...", "....BB", "......"));

        assertEquals(Direction.RIGHT, board.getExit(Side.HUMAN));
        assertEquals(Direction.LEFT, board.getExit(Side.MACHINE));
    }

    @Test
    void tooFewLinesAreRejected() {
        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse("4 6\nA RIGHT\n"));

        assertTrue(e.getMessage().contains("hiányos"));
    }

    @ParameterizedTest
    @CsvSource({"abc", "4", "4 x", "4 6 7"})
    void malformedSizeLineIsRejected(String sizeLine) {
        String input = text(sizeLine, "A RIGHT", "B LEFT", "C.....", "CAA...", "....BB", "......");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("méret"));
    }

    @Test
    void hugeSizeIsRejected() {
        String input = text("99999999999 6", "A RIGHT", "B LEFT", "C.....");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("Túl nagy"));
    }

    @ParameterizedTest
    @CsvSource({"A RIGHTWARDS", "C RIGHT", "A", "A 5"})
    void malformedExitLineIsRejected(String exitLine) {
        String input = text("4 6", exitLine, "B LEFT", "C.....", "CAA...", "....BB", "......");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("kijárat"));
    }

    @Test
    void sameExitTwiceIsRejected() {
        String input = text("4 6", "A RIGHT", "A LEFT", "C.....", "CAA...", "....BB", "......");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("kétszer"));
    }

    @Test
    void wrongNumberOfGridRowsIsRejected() {
        String input = text("4 6", "A RIGHT", "B LEFT", "C.....", "CAA...", "....BB");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("4 sora"));
    }

    @Test
    void wrongRowLengthIsRejected() {
        String input = text("4 6", "A RIGHT", "B LEFT", "C.....", "CAA..", "....BB", "......");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("2. rácssor"));
    }

    @Test
    void invalidGridCharacterIsRejected() {
        String input = text("4 6", "A RIGHT", "B LEFT", "C.....", "CAA..?", "....BB", "......");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("Érvénytelen karakter"));
    }

    @Test
    void singleCellVehicleIsRejected() {
        String input = text("4 6", "A RIGHT", "B LEFT", "C.....", ".AA...", "....BB", ".....D");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("1 mezőből"));
    }

    @Test
    void vehicleWithAGapIsRejected() {
        String input = text("4 6", "A RIGHT", "B LEFT", "C.....", "CA.A..", "....BB", "......");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("nem összefüggőek"));
    }

    @Test
    void bentVehicleIsRejected() {
        String input = text("4 6", "A RIGHT", "B LEFT", "C.....", "CAA...", "....BB", ".....C");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("nem egy sorban vagy oszlopban"));
    }

    @Test
    void missingHeroVehicleIsRejected() {
        String input = text("4 6", "A RIGHT", "B LEFT", "C.....", "CAA...", "......", "......");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("B céljármű"));
    }

    @Test
    void alreadySolvedBoardIsRejected() {
        String input = text("2 4", "A RIGHT", "B LEFT", "..AA", ".BB.");

        InvalidBoardException e = assertThrows(InvalidBoardException.class, () -> reader.parse(input));

        assertTrue(e.getMessage().contains("megoldott"));
    }
}
