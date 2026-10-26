package hu.nye.progtech.rushhour.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DirectionTest {

    @ParameterizedTest
    @CsvSource({
        "up,UP", "DOWN,DOWN", "left,LEFT", "Right,RIGHT",
        "fel,UP", "le,DOWN", "balra,LEFT", "jobbra,RIGHT"
    })
    void fromTextRecognizesEnglishAndHungarianNames(String text, Direction expected) {
        assertEquals(expected, Direction.fromText(text));
    }

    @Test
    void fromTextIgnoresSurroundingWhitespace() {
        assertEquals(Direction.UP, Direction.fromText("  up "));
    }

    @Test
    void fromTextRejectsUnknownText() {
        assertThrows(IllegalArgumentException.class, () -> Direction.fromText("diagonal"));
    }

    @Test
    void upAndDownAreVerticalLeftAndRightAreHorizontal() {
        assertEquals(Orientation.VERTICAL, Direction.UP.getOrientation());
        assertEquals(Orientation.VERTICAL, Direction.DOWN.getOrientation());
        assertEquals(Orientation.HORIZONTAL, Direction.LEFT.getOrientation());
        assertEquals(Orientation.HORIZONTAL, Direction.RIGHT.getOrientation());
    }

    @Test
    void downAndRightPointTowardsGrowingIndexes() {
        assertTrue(Direction.DOWN.isForward());
        assertTrue(Direction.RIGHT.isForward());
        assertFalse(Direction.UP.isForward());
        assertFalse(Direction.LEFT.isForward());
    }

    @Test
    void deltasMatchTheDirection() {
        assertEquals(-1, Direction.UP.getRowDelta());
        assertEquals(1, Direction.DOWN.getRowDelta());
        assertEquals(-1, Direction.LEFT.getColumnDelta());
        assertEquals(1, Direction.RIGHT.getColumnDelta());
        assertEquals("balra", Direction.LEFT.getLabel());
    }
}
