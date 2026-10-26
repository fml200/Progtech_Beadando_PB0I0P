package hu.nye.progtech.rushhour.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class VehicleTest {

    private final Vehicle horizontal = new Vehicle('A', new Position(1, 2), 3, Orientation.HORIZONTAL);
    private final Vehicle vertical = new Vehicle('C', new Position(0, 4), 2, Orientation.VERTICAL);

    @Test
    void horizontalVehicleOccupiesCellsToTheRight() {
        assertEquals(List.of(new Position(1, 2), new Position(1, 3), new Position(1, 4)), horizontal.cells());
        assertEquals(new Position(1, 4), horizontal.end());
    }

    @Test
    void verticalVehicleOccupiesCellsDownwards() {
        assertEquals(List.of(new Position(0, 4), new Position(1, 4)), vertical.cells());
        assertEquals(new Position(1, 4), vertical.end());
    }

    @Test
    void occupiesTellsWhetherACellBelongsToTheVehicle() {
        assertTrue(horizontal.occupies(new Position(1, 3)));
        assertFalse(horizontal.occupies(new Position(1, 5)));
        assertFalse(horizontal.occupies(new Position(2, 3)));
    }

    @Test
    void movedReturnsANewVehicleAndKeepsTheOriginal() {
        Vehicle moved = horizontal.moved(Direction.RIGHT, 2);

        assertEquals(new Position(1, 4), moved.start());
        assertEquals(new Position(1, 2), horizontal.start());
        assertEquals(horizontal.length(), moved.length());
        assertEquals(horizontal.orientation(), moved.orientation());
    }

    @Test
    void movedBackwardsDecreasesTheCoordinate() {
        assertEquals(new Position(1, 1), horizontal.moved(Direction.LEFT, 1).start());
        assertEquals(new Position(0, 4), vertical.moved(Direction.DOWN, 1).moved(Direction.UP, 1).start());
    }

    @Test
    void movedAlongTheWrongAxisIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> horizontal.moved(Direction.UP, 1));
        assertThrows(IllegalArgumentException.class, () -> vertical.moved(Direction.LEFT, 1));
    }

    @Test
    void identifierMustBeAnUpperCaseLetter() {
        Position start = new Position(0, 0);

        assertThrows(IllegalArgumentException.class, () -> new Vehicle('a', start, 2, Orientation.HORIZONTAL));
        assertThrows(IllegalArgumentException.class, () -> new Vehicle('1', start, 2, Orientation.HORIZONTAL));
    }

    @Test
    void lengthMustBeAtLeastTwo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle('A', new Position(0, 0), 1, Orientation.HORIZONTAL));
    }

    @Test
    void startAndOrientationAreRequired() {
        assertThrows(NullPointerException.class, () -> new Vehicle('A', null, 2, Orientation.HORIZONTAL));
        assertThrows(NullPointerException.class, () -> new Vehicle('A', new Position(0, 0), 2, null));
    }

    @Test
    void vehiclesWithTheSameDataAreEqual() {
        assertEquals(new Vehicle('A', new Position(1, 2), 3, Orientation.HORIZONTAL), horizontal);
    }
}
