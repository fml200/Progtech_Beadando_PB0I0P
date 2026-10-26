package hu.nye.progtech.rushhour.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Move;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Side;

class DefaultBoardsTest {

    private final Board board = DefaultBoards.standard();

    @Test
    void isASixByTwelveBoardWithBothExits() {
        assertEquals(6, board.getRows());
        assertEquals(12, board.getColumns());
        assertEquals(Direction.RIGHT, board.getExit(Side.HUMAN));
        assertEquals(Direction.LEFT, board.getExit(Side.MACHINE));
    }

    @Test
    void startsUnsolvedWithPossibleMoves() {
        assertTrue(board.findWinner().isEmpty());
        assertFalse(board.getPossibleMoves().isEmpty());
    }

    @Test
    void theHumanHeroCanOnlySlideOneCellAtTheStart() {
        assertEquals(1, board.getMaxSlide('A', Direction.RIGHT));
    }

    @Test
    void theBoardCanBeSolvedByTheHumanInThreeMoves() {
        Board solved = board
                .apply(new Move('C', Direction.DOWN, 2))
                .apply(new Move('E', Direction.DOWN, 1))
                .apply(new Move('A', Direction.RIGHT, 10));

        assertEquals(Side.HUMAN, solved.findWinner().orElseThrow());
        assertEquals(new Position(2, 10), solved.getVehicle('A').orElseThrow().start());
    }
}
