package hu.nye.progtech.rushhour.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.nye.progtech.rushhour.TestBoards;
import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Move;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Side;

class GameSessionTest {

    private MachinePlayer machine;
    private GameSession session;

    @BeforeEach
    void setUp() {
        machine = mock(MachinePlayer.class);
        session = new GameSession(TestBoards.simple(), machine);
    }

    @Test
    void startsWithTheHumanOnMoveAndNoWinner() {
        assertEquals(Side.HUMAN, session.getCurrentSide());
        assertFalse(session.isFinished());
        assertTrue(session.getWinner().isEmpty());
        assertEquals(0, session.getMovesPlayed());
        assertEquals(TestBoards.simple(), session.getBoard());
    }

    @Test
    void humanMoveChangesTheBoardAndPassesTheTurn() {
        session.applyHumanMove(new Move('A', Direction.RIGHT, 1));

        assertEquals(new Position(1, 2), session.getBoard().getVehicle('A').orElseThrow().start());
        assertEquals(Side.MACHINE, session.getCurrentSide());
        assertEquals(1, session.getMovesPlayed());
    }

    @Test
    void illegalHumanMoveKeepsTheTurnAndTheBoard() {
        assertThrows(InvalidMoveException.class, () -> session.applyHumanMove(new Move('A', Direction.LEFT, 1)));

        assertEquals(Side.HUMAN, session.getCurrentSide());
        assertEquals(0, session.getMovesPlayed());
        assertEquals(TestBoards.simple(), session.getBoard());
    }

    @Test
    void humanCannotMoveTwiceInARow() {
        session.applyHumanMove(new Move('A', Direction.RIGHT, 1));

        InvalidMoveException e = assertThrows(InvalidMoveException.class,
                () -> session.applyHumanMove(new Move('A', Direction.RIGHT, 1)));

        assertTrue(e.getMessage().contains("nem a(z) játékos"));
    }

    @Test
    void machineCannotMoveBeforeTheHuman() {
        assertThrows(InvalidMoveException.class, () -> session.playMachineMove());

        verify(machine, never()).chooseMove(any());
    }

    @Test
    void machineMoveIsAppliedAndThePlayerIsNext() {
        Move reply = new Move('B', Direction.LEFT, 1);
        when(machine.chooseMove(any(Board.class))).thenReturn(Optional.of(reply));
        session.applyHumanMove(new Move('A', Direction.RIGHT, 1));

        Optional<Move> played = session.playMachineMove();

        assertEquals(Optional.of(reply), played);
        assertEquals(new Position(2, 3), session.getBoard().getVehicle('B').orElseThrow().start());
        assertEquals(Side.HUMAN, session.getCurrentSide());
        assertEquals(2, session.getMovesPlayed());
    }

    @Test
    void machineWithoutAMoveSkipsItsTurn() {
        when(machine.chooseMove(any(Board.class))).thenReturn(Optional.empty());
        session.applyHumanMove(new Move('A', Direction.RIGHT, 1));

        Optional<Move> played = session.playMachineMove();

        assertTrue(played.isEmpty());
        assertEquals(Side.HUMAN, session.getCurrentSide());
        assertEquals(1, session.getMovesPlayed());
    }

    @Test
    void humanWinsByReachingTheExit() {
        session.applyHumanMove(new Move('A', Direction.RIGHT, 3));

        assertTrue(session.isFinished());
        assertEquals(Side.HUMAN, session.getWinner().orElseThrow());
    }

    @Test
    void machineWinsByReachingItsExit() {
        when(machine.chooseMove(any(Board.class))).thenReturn(Optional.of(new Move('B', Direction.LEFT, 4)));
        session.applyHumanMove(new Move('A', Direction.RIGHT, 1));

        session.playMachineMove();

        assertTrue(session.isFinished());
        assertEquals(Side.MACHINE, session.getWinner().orElseThrow());
    }

    @Test
    void noMovesAreAcceptedAfterTheGameIsOver() {
        session.applyHumanMove(new Move('A', Direction.RIGHT, 3));

        InvalidMoveException human = assertThrows(InvalidMoveException.class,
                () -> session.applyHumanMove(new Move('C', Direction.DOWN, 1)));
        InvalidMoveException computer = assertThrows(InvalidMoveException.class, () -> session.playMachineMove());

        assertTrue(human.getMessage().contains("véget ért"));
        assertTrue(computer.getMessage().contains("véget ért"));
    }

    @Test
    void sessionStartedOnASolvedBoardIsAlreadyFinished() {
        GameSession solved = new GameSession(TestBoards.solved(), machine);

        assertTrue(solved.isFinished());
        assertEquals(Side.HUMAN, solved.getWinner().orElseThrow());
    }

    @Test
    void constructorArgumentsAreRequired() {
        assertThrows(NullPointerException.class, () -> new GameSession(null, machine));
        assertThrows(NullPointerException.class, () -> new GameSession(TestBoards.simple(), null));
    }
}
