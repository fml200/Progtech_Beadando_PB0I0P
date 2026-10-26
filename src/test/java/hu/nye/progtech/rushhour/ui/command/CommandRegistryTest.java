package hu.nye.progtech.rushhour.ui.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CommandRegistryTest {

    private final CommandRegistry registry = new CommandRegistry();

    @Test
    void knowsEveryCommand() {
        List<String> names = registry.getAllCommands().stream().map(Command::getName).toList();

        assertEquals(List.of("move", "save", "load", "new", "print", "help", "exit"), names);
    }

    @Test
    void commandListCannotBeModified() {
        List<Command> commands = registry.getAllCommands();

        assertThrows(UnsupportedOperationException.class, () -> commands.add(new ExitCommand()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"move A right 1", "MOVE a RIGHT 2", "  move   b  left   10  ", "move C fel 1",
        "move c le 3", "move D balra 1", "move E jobbra 4", "move F up 1", "move G down 1"})
    void recognizesMoveInAllItsForms(String input) {
        assertInstanceOf(MoveCommand.class, registry.find(input).orElseThrow().command());
    }

    @Test
    void recognizesTheOtherCommands() {
        assertInstanceOf(SaveCommand.class, registry.find("save allas.txt").orElseThrow().command());
        assertInstanceOf(LoadCommand.class, registry.find("load palya.txt").orElseThrow().command());
        assertInstanceOf(NewGameCommand.class, registry.find("new").orElseThrow().command());
        assertInstanceOf(PrintCommand.class, registry.find("print").orElseThrow().command());
        assertInstanceOf(HelpCommand.class, registry.find("HELP").orElseThrow().command());
        assertInstanceOf(ExitCommand.class, registry.find("exit").orElseThrow().command());
        assertInstanceOf(ExitCommand.class, registry.find("quit").orElseThrow().command());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "foo", "move", "move A", "move A right", "move A right x", "move A diagonal 1",
        "move AB right 1", "move 1 right 1", "move A right -1", "save", "save a b", "load", "new game",
        "print now", "help me", "exit now"})
    void rejectsUnknownOrMalformedInput(String input) {
        assertTrue(registry.find(input).isEmpty());
    }

    @Test
    void matchedCommandCarriesTheParameters() {
        CommandRegistry.MatchedCommand found = registry.find("move c Down 3").orElseThrow();

        assertEquals("c", found.matcher().group(1));
        assertEquals("Down", found.matcher().group(2));
        assertEquals("3", found.matcher().group(3));
    }

    @Test
    void movingAndSavingAreOnlyAvailableWhilePlaying() {
        List<String> playing = registry.getCommandsForPhase(GamePhase.PLAYING).stream().map(Command::getName).toList();
        List<String> finished = registry.getCommandsForPhase(GamePhase.FINISHED).stream().map(Command::getName)
                .toList();

        assertEquals(List.of("move", "save", "load", "new", "print", "help", "exit"), playing);
        assertEquals(List.of("load", "new", "print", "help", "exit"), finished);
    }

    @Test
    void commandsDescribeThemselves() {
        Command move = registry.find("move A right 1").orElseThrow().command();

        assertEquals("move", move.getName());
        assertEquals("move <jármű> <irány> <db>", move.getUsage());
        assertFalse(move.getDescription().isBlank());
        assertTrue(move.isAllowedIn(GamePhase.PLAYING));
        assertFalse(move.isAllowedIn(GamePhase.FINISHED));
    }
}
