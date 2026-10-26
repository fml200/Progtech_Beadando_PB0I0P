package hu.nye.progtech.rushhour.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import hu.nye.progtech.rushhour.TestBoards;
import hu.nye.progtech.rushhour.exception.InvalidBoardException;
import hu.nye.progtech.rushhour.exception.InvalidMoveException;

class BoardTest {

    private static final Map<Side, Direction> EXITS = Map.of(Side.HUMAN, Direction.RIGHT, Side.MACHINE, Direction.LEFT);

    private final Board board = TestBoards.simple();

    private static Vehicle horizontal(char id, int row, int column, int length) {
        return new Vehicle(id, new Position(row, column), length, Orientation.HORIZONTAL);
    }

    @Test
    void hasTheGivenSizeAndVehiclesSortedById() {
        assertEquals(4, board.getRows());
        assertEquals(6, board.getColumns());
        assertEquals(List.of('A', 'B', 'C'), board.getVehicles().stream().map(Vehicle::id).toList());
    }

    @Test
    void vehicleCollectionCannotBeModified() {
        assertThrows(UnsupportedOperationException.class, () -> board.getVehicles().clear());
    }

    @Test
    void findsVehiclesById() {
        assertTrue(board.getVehicle('A').isPresent());
        assertTrue(board.getVehicle('Z').isEmpty());
    }

    @Test
    void exitIsAtTheEdgeOfTheHeroLine() {
        assertEquals(Direction.RIGHT, board.getExit(Side.HUMAN));
        assertEquals(new Position(1, 5), board.getExitPosition(Side.HUMAN));
        assertEquals(new Position(2, 0), board.getExitPosition(Side.MACHINE));
    }

    @Test
    void verticalExitsAreAtTheTopAndBottomEdge() {
        Board vertical = TestBoards.verticalExits();

        assertEquals(new Position(4, 1), vertical.getExitPosition(Side.HUMAN));
        assertEquals(new Position(0, 3), vertical.getExitPosition(Side.MACHINE));
    }

    @Test
    void knowsWhichCellsAreInsideAndFree() {
        assertTrue(board.isInside(new Position(0, 0)));
        assertTrue(board.isInside(new Position(3, 5)));
        assertFalse(board.isInside(new Position(4, 0)));
        assertFalse(board.isInside(new Position(-1, 0)));
        assertFalse(board.isInside(new Position(0, 6)));
        assertFalse(board.isInside(new Position(0, -1)));

        assertTrue(board.isFree(new Position(0, 1)));
        assertFalse(board.isFree(new Position(0, 0)));
        assertFalse(board.isFree(new Position(9, 9)));
    }

    @Test
    void findsTheVehicleAtACell() {
        assertEquals('A', board.getVehicleAt(new Position(1, 1)).orElseThrow().id());
        assertTrue(board.getVehicleAt(new Position(3, 3)).isEmpty());
        assertTrue(board.getVehicleAt(new Position(9, 9)).isEmpty());
    }

    @Test
    void maxSlideCountsTheFreeCellsInFront() {
        assertEquals(3, board.getMaxSlide('A', Direction.RIGHT));
        assertEquals(0, board.getMaxSlide('A', Direction.LEFT));
        assertEquals(2, board.getMaxSlide('C', Direction.DOWN));
        assertEquals(0, board.getMaxSlide('C', Direction.UP));
        assertEquals(4, board.getMaxSlide('B', Direction.LEFT));
        assertEquals(0, board.getMaxSlide('B', Direction.RIGHT));
    }

    @Test
    void maxSlideRejectsUnknownVehicleAndWrongAxis() {
        assertThrows(InvalidMoveException.class, () -> board.getMaxSlide('Z', Direction.UP));
        assertThrows(InvalidMoveException.class, () -> board.getMaxSlide('A', Direction.UP));
    }

    @Test
    void listsAllPossibleMoves() {
        List<Move> moves = board.getPossibleMoves();

        assertEquals(9, moves.size());
        assertTrue(moves.contains(new Move('A', Direction.RIGHT, 3)));
        assertTrue(moves.contains(new Move('B', Direction.LEFT, 1)));
        assertTrue(moves.contains(new Move('C', Direction.DOWN, 2)));
    }

    @Test
    void listsThePossibleMovesOfOneVehicle() {
        assertEquals(List.of(new Move('C', Direction.DOWN, 1), new Move('C', Direction.DOWN, 2)),
                board.getPossibleMoves('C'));
        assertThrows(InvalidMoveException.class, () -> board.getPossibleMoves('Z'));
    }

    @Test
    void deadlockedBoardHasNoMoves() {
        assertTrue(TestBoards.deadlocked().getPossibleMoves().isEmpty());
    }

    @Test
    void applyReturnsANewBoardAndKeepsTheOriginal() {
        Board moved = board.apply(new Move('A', Direction.RIGHT, 2));

        assertEquals(new Position(1, 3), moved.getVehicle('A').orElseThrow().start());
        assertEquals(new Position(1, 1), board.getVehicle('A').orElseThrow().start());
        assertTrue(moved.findWinner().isEmpty());
    }

    @Test
    void humanWinsWhenTheHeroReachesItsExit() {
        Board won = board.apply(new Move('A', Direction.RIGHT, 3));

        assertTrue(won.hasReachedExit(Side.HUMAN));
        assertFalse(won.hasReachedExit(Side.MACHINE));
        assertEquals(Side.HUMAN, won.findWinner().orElseThrow());
    }

    @Test
    void machineWinsWhenItsHeroReachesItsExit() {
        Board won = board.apply(new Move('B', Direction.LEFT, 4));

        assertEquals(Side.MACHINE, won.findWinner().orElseThrow());
    }

    @Test
    void anyVehicleCanBeMovedNotOnlyTheHeroes() {
        Board moved = board.apply(new Move('C', Direction.DOWN, 2));

        assertEquals(new Position(2, 0), moved.getVehicle('C').orElseThrow().start());
    }

    @Test
    void startingBoardHasNoWinner() {
        assertTrue(board.findWinner().isEmpty());
    }

    @Test
    void applyRejectsIllegalMoves() {
        InvalidMoveException unknown = assertThrows(InvalidMoveException.class,
                () -> board.apply(new Move('Z', Direction.RIGHT, 1)));
        InvalidMoveException axis = assertThrows(InvalidMoveException.class,
                () -> board.apply(new Move('A', Direction.UP, 1)));
        InvalidMoveException zero = assertThrows(InvalidMoveException.class,
                () -> board.apply(new Move('A', Direction.RIGHT, 0)));
        InvalidMoveException blocked = assertThrows(InvalidMoveException.class,
                () -> board.apply(new Move('A', Direction.LEFT, 1)));
        InvalidMoveException tooFar = assertThrows(InvalidMoveException.class,
                () -> board.apply(new Move('A', Direction.RIGHT, 4)));

        assertEquals("Nincs ilyen jármű: Z", unknown.getMessage());
        assertTrue(axis.getMessage().contains("vízszintesen"));
        assertTrue(zero.getMessage().contains("legalább 1"));
        assertTrue(blocked.getMessage().contains("nem mozdulhat balra"));
        assertTrue(tooFar.getMessage().contains("legfeljebb 3"));
    }

    @Test
    void overlappingVehiclesAreRejected() {
        List<Vehicle> vehicles = List.of(horizontal('A', 0, 0, 2), horizontal('B', 0, 1, 2));

        InvalidBoardException e = assertThrows(InvalidBoardException.class,
                () -> new Board(4, 6, vehicles, EXITS));

        assertTrue(e.getMessage().contains("átfedi"));
    }

    @Test
    void vehicleOutsideTheBoardIsRejected() {
        List<Vehicle> vehicles = List.of(horizontal('A', 0, 5, 2), horizontal('B', 1, 0, 2));

        InvalidBoardException e = assertThrows(InvalidBoardException.class,
                () -> new Board(4, 6, vehicles, EXITS));

        assertTrue(e.getMessage().contains("kilóg"));
    }

    @Test
    void duplicateIdentifiersAreRejected() {
        List<Vehicle> vehicles = List.of(horizontal('A', 0, 0, 2), horizontal('A', 1, 0, 2));

        assertThrows(InvalidBoardException.class, () -> new Board(4, 6, vehicles, EXITS));
    }

    @Test
    void missingHeroVehicleIsRejected() {
        List<Vehicle> vehicles = List.of(horizontal('A', 0, 0, 2));

        InvalidBoardException e = assertThrows(InvalidBoardException.class,
                () -> new Board(4, 6, vehicles, EXITS));

        assertTrue(e.getMessage().contains("B céljármű"));
    }

    @Test
    void missingExitIsRejected() {
        List<Vehicle> vehicles = List.of(horizontal('A', 0, 0, 2), horizontal('B', 1, 0, 2));
        Map<Side, Direction> onlyHuman = Map.of(Side.HUMAN, Direction.RIGHT);

        InvalidBoardException e = assertThrows(InvalidBoardException.class,
                () -> new Board(4, 6, vehicles, onlyHuman));

        assertTrue(e.getMessage().contains("kijárata"));
    }

    @Test
    void exitAcrossTheHeroAxisIsRejected() {
        List<Vehicle> vehicles = List.of(horizontal('A', 0, 0, 2), horizontal('B', 1, 0, 2));
        Map<Side, Direction> exits = Map.of(Side.HUMAN, Direction.UP, Side.MACHINE, Direction.LEFT);

        InvalidBoardException e = assertThrows(InvalidBoardException.class,
                () -> new Board(4, 6, vehicles, exits));

        assertTrue(e.getMessage().contains("tengelyébe"));
    }

    @Test
    void sizeMustBeBetweenOneAndTheMaximum() {
        List<Vehicle> vehicles = List.of(horizontal('A', 0, 0, 2), horizontal('B', 1, 0, 2));

        assertThrows(InvalidBoardException.class, () -> new Board(0, 6, vehicles, EXITS));
        assertThrows(InvalidBoardException.class, () -> new Board(4, 0, vehicles, EXITS));
        assertThrows(InvalidBoardException.class, () -> new Board(Board.MAX_SIZE + 1, 6, vehicles, EXITS));
        assertThrows(InvalidBoardException.class, () -> new Board(4, Board.MAX_SIZE + 1, vehicles, EXITS));
    }

    @Test
    void constructorArgumentsAreRequired() {
        assertThrows(NullPointerException.class, () -> new Board(4, 6, null, EXITS));
        assertThrows(NullPointerException.class, () -> new Board(4, 6, List.of(), null));
    }

    @Test
    void equalBoardsHaveEqualHashCodes() {
        Board same = TestBoards.simple();

        assertEquals(board, same);
        assertEquals(board.hashCode(), same.hashCode());
        assertEquals(board, board);
    }

    @Test
    void differentBoardsAreNotEqual() {
        assertNotEquals(board, board.apply(new Move('A', Direction.RIGHT, 1)));
        assertNotEquals(board, TestBoards.verticalExits());
        assertNotEquals(board, null);
        assertNotEquals(board, "board");
    }

    @Test
    void toStringDescribesTheBoard() {
        String text = board.toString();

        assertTrue(text.startsWith("Board{rows=4, columns=6"));
        assertTrue(text.contains("exits="));
    }
}
