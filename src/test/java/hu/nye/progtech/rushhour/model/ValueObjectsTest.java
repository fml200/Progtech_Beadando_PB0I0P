package hu.nye.progtech.rushhour.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ValueObjectsTest {

    @Test
    void positionShiftsInTheGivenDirection() {
        Position start = new Position(2, 3);

        assertEquals(new Position(2, 6), start.shifted(Direction.RIGHT, 3));
        assertEquals(new Position(1, 3), start.shifted(Direction.UP, 1));
        assertEquals(start, start.shifted(Direction.DOWN, 0));
    }

    @Test
    void positionsWithTheSameCoordinatesAreEqual() {
        assertEquals(new Position(1, 2), new Position(1, 2));
        assertEquals(new Position(1, 2).hashCode(), new Position(1, 2).hashCode());
    }

    @Test
    void moveRequiresADirection() {
        assertThrows(NullPointerException.class, () -> new Move('A', null, 1));
    }

    @Test
    void sidesAreOpponentsOfEachOther() {
        assertEquals(Side.MACHINE, Side.HUMAN.opponent());
        assertEquals(Side.HUMAN, Side.MACHINE.opponent());
    }

    @Test
    void sidesHaveTheirHeroVehicle() {
        assertEquals('A', Side.HUMAN.getHeroId());
        assertEquals('B', Side.MACHINE.getHeroId());
        assertEquals("Gép", Side.MACHINE.getLabel());
    }

    @Test
    void orientationsHaveHungarianLabels() {
        assertEquals("vízszintesen", Orientation.HORIZONTAL.getLabel());
        assertEquals("függőlegesen", Orientation.VERTICAL.getLabel());
    }
}
