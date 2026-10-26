package hu.nye.progtech.rushhour.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import hu.nye.progtech.rushhour.exception.InvalidBoardException;

class BoardBuilderTest {

    @Test
    void buildsTheDescribedBoard() {
        Board board = new BoardBuilder(3, 5)
                .humanExit(Direction.RIGHT)
                .machineExit(Direction.DOWN)
                .horizontal('A', 0, 0, 2)
                .vertical('B', 1, 4, 2)
                .build();

        assertEquals(3, board.getRows());
        assertEquals(5, board.getColumns());
        assertEquals(Orientation.HORIZONTAL, board.getVehicle('A').orElseThrow().orientation());
        assertEquals(Orientation.VERTICAL, board.getVehicle('B').orElseThrow().orientation());
        assertEquals(Direction.DOWN, board.getExit(Side.MACHINE));
    }

    @Test
    void buildFailsForAnInvalidBoard() {
        BoardBuilder builder = new BoardBuilder(3, 5).horizontal('A', 0, 0, 2);

        assertThrows(InvalidBoardException.class, builder::build);
    }
}
