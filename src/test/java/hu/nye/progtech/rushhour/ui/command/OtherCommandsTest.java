package hu.nye.progtech.rushhour.ui.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import hu.nye.progtech.rushhour.TestBoards;
import hu.nye.progtech.rushhour.exception.BoardFileException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Move;

class OtherCommandsTest {

    private final CommandFixture fixture = new CommandFixture();

    @Test
    void saveWritesTheCurrentBoardToTheFile() {
        fixture.run(new SaveCommand(), "save allas.txt");

        verify(fixture.repository).save(fixture.context.getSession().getBoard(), "allas.txt");
        assertTrue(fixture.output().contains("Állás elmentve: allas.txt"));
    }

    @Test
    void saveReportsFileProblems() {
        doThrow(new BoardFileException("A fájl nem írható: x", new IOException()))
                .when(fixture.repository).save(TestBoards.simple(), "x");

        assertThrows(BoardFileException.class, () -> fixture.run(new SaveCommand(), "save x"));
    }

    @Test
    void loadStartsAFreshGameOnTheLoadedBoard() {
        Board loaded = TestBoards.verticalExits();
        when(fixture.repository.load("palya.txt")).thenReturn(loaded);
        fixture.context.getSession().applyHumanMove(new Move('A', Direction.RIGHT, 1));

        fixture.run(new LoadCommand(), "load palya.txt");

        assertSame(loaded, fixture.context.getSession().getBoard());
        assertEquals(0, fixture.context.getSession().getMovesPlayed());
        assertTrue(fixture.output().contains("Pálya betöltve: palya.txt"));
        assertTrue(fixture.output().contains("    a b c d e"));
    }

    @Test
    void loadIsAvailableInEveryPhase() {
        assertTrue(new LoadCommand().isAllowedIn(GamePhase.PLAYING));
        assertTrue(new LoadCommand().isAllowedIn(GamePhase.FINISHED));
    }

    @Test
    void newGameRestartsFromTheInitialBoard() {
        fixture.context.getSession().applyHumanMove(new Move('A', Direction.RIGHT, 3));
        assertTrue(fixture.context.getSession().isFinished());

        fixture.run(new NewGameCommand(), "new");

        assertFalse(fixture.context.getSession().isFinished());
        assertEquals(TestBoards.simple(), fixture.context.getSession().getBoard());
        assertEquals(0, fixture.context.getSession().getMovesPlayed());
        assertTrue(fixture.output().contains("Új játék indult."));
    }

    @Test
    void printShowsTheBoardAndTheMoveCount() {
        fixture.context.getSession().applyHumanMove(new Move('A', Direction.RIGHT, 1));

        fixture.run(new PrintCommand(), "print");

        assertTrue(fixture.output().contains("    a b c d e f"));
        assertTrue(fixture.output().contains("Eddigi lépések száma: 1"));
    }

    @Test
    void helpListsTheCommandsOfTheCurrentPhase() {
        fixture.run(new HelpCommand(new CommandRegistry()), "help");

        String output = fixture.output();
        assertTrue(output.contains("Használható parancsok:"));
        assertTrue(output.contains("move <jármű> <irány> <db>"));
        assertTrue(output.contains("save <fájl>"));
        assertTrue(output.contains("exit"));
    }

    @Test
    void helpOmitsMovingAndSavingWhenTheGameIsOver() {
        fixture.context.getSession().applyHumanMove(new Move('A', Direction.RIGHT, 3));

        fixture.run(new HelpCommand(new CommandRegistry()), "help");

        String output = fixture.output();
        assertFalse(output.contains("move <"));
        assertFalse(output.contains("save <"));
        assertTrue(output.contains("new"));
    }

    @Test
    void exitRequestsTheEndOfTheProgram() {
        assertFalse(fixture.context.isExitRequested());

        fixture.run(new ExitCommand(), "exit");

        assertTrue(fixture.context.isExitRequested());
        assertTrue(fixture.output().contains("Viszlát!"));
    }

    @Test
    void contextReportsThePhaseAndThePlayerName() {
        assertEquals(GamePhase.PLAYING, fixture.context.getPhase());
        assertEquals("Játékos", fixture.context.getPlayerName());

        fixture.context.getSession().applyHumanMove(new Move('A', Direction.RIGHT, 3));
        fixture.context.setPlayerName("Anna");

        assertEquals(GamePhase.FINISHED, fixture.context.getPhase());
        assertEquals("Anna", fixture.context.getPlayerName());
    }
}
