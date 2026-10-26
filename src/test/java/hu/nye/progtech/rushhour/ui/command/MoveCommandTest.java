package hu.nye.progtech.rushhour.ui.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Move;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Side;

class MoveCommandTest {

    private final CommandFixture fixture = new CommandFixture();
    private final MoveCommand command = new MoveCommand();

    private void machineReplies(Move move) {
        when(fixture.machine.chooseMove(any(Board.class))).thenReturn(Optional.of(move));
    }

    @Test
    void humanMoveIsFollowedByTheMachineReplyAndTheBoard() {
        machineReplies(new Move('B', Direction.LEFT, 1));

        fixture.run(command, "move A right 1");

        String output = fixture.output();
        assertTrue(output.contains("Te: A jármű jobbra, 1 mező"), output);
        assertTrue(output.contains("Gép: B jármű balra, 1 mező"), output);
        assertTrue(output.contains("    a b c d e f"), output);
        assertEquals(2, fixture.context.getSession().getMovesPlayed());
        assertEquals(new Position(1, 2),
                fixture.context.getSession().getBoard().getVehicle('A').orElseThrow().start());
    }

    @Test
    void theVehicleLetterIsCaseInsensitiveAndDirectionsCanBeHungarian() {
        machineReplies(new Move('B', Direction.LEFT, 1));

        fixture.run(command, "MOVE a JOBBRA 2");

        assertEquals(new Position(1, 3),
                fixture.context.getSession().getBoard().getVehicle('A').orElseThrow().start());
    }

    @Test
    void machineThatCannotMoveSkipsItsTurn() {
        fixture.run(command, "move A right 1");

        assertTrue(fixture.output().contains("A gép nem tud lépni, kimarad."));
        assertEquals(1, fixture.context.getSession().getMovesPlayed());
        assertEquals(Side.HUMAN, fixture.context.getSession().getCurrentSide());
    }

    @Test
    void humanWinAnnouncesTheWinnerWithoutAMachineMove() {
        fixture.run(command, "move A right 3");

        String output = fixture.output();
        assertTrue(output.contains("Gratulálok, Játékos! Az A jármű elérte a kijáratot, nyertél!"), output);
        assertFalse(output.contains("Gép:"));
        verifyNoInteractions(fixture.machine);
        assertTrue(fixture.context.getSession().isFinished());
    }

    @Test
    void theWinnerIsCongratulatedByName() {
        fixture.context.setPlayerName("Anna");

        fixture.run(command, "move A right 3");

        assertTrue(fixture.output().contains("Gratulálok, Anna!"));
    }

    @Test
    void machineWinIsAnnounced() {
        machineReplies(new Move('B', Direction.LEFT, 4));

        fixture.run(command, "move A right 1");

        String output = fixture.output();
        assertTrue(output.contains("A gép nyert"), output);
        assertEquals(Side.MACHINE, fixture.context.getSession().getWinner().orElseThrow());
    }

    @Test
    void blockedMoveIsRejectedWithoutTouchingTheGame() {
        InvalidMoveException e = assertThrows(InvalidMoveException.class,
                () -> fixture.run(command, "move A left 1"));

        assertTrue(e.getMessage().contains("nem mozdulhat balra"));
        assertEquals(0, fixture.context.getSession().getMovesPlayed());
        verifyNoInteractions(fixture.machine);
    }

    @Test
    void unknownVehicleIsRejected() {
        InvalidMoveException e = assertThrows(InvalidMoveException.class,
                () -> fixture.run(command, "move Z right 1"));

        assertEquals("Nincs ilyen jármű: Z", e.getMessage());
    }

    @Test
    void movingAcrossTheAxisIsRejected() {
        InvalidMoveException e = assertThrows(InvalidMoveException.class,
                () -> fixture.run(command, "move A up 1"));

        assertTrue(e.getMessage().contains("vízszintesen"));
    }

    @Test
    void tooManyStepsAreRejected() {
        InvalidMoveException e = assertThrows(InvalidMoveException.class,
                () -> fixture.run(command, "move A right 4"));

        assertTrue(e.getMessage().contains("legfeljebb 3"));
    }

    @Test
    void zeroStepsAreRejected() {
        assertThrows(InvalidMoveException.class, () -> fixture.run(command, "move A right 0"));
    }

    @Test
    void absurdlyLargeStepCountIsRejected() {
        InvalidMoveException e = assertThrows(InvalidMoveException.class,
                () -> fixture.run(command, "move A right 99999999999"));

        assertTrue(e.getMessage().contains("Túl nagy lépésszám"));
    }
}
