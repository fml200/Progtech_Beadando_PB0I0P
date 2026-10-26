package hu.nye.progtech.rushhour.ui.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

/** Az összes parancs nyilvántartása és a bemenethez illő parancs megkeresése. */
public final class CommandRegistry {

    private final List<Command> commands = new ArrayList<>();

    public CommandRegistry() {
        commands.add(new MoveCommand());
        commands.add(new SaveCommand());
        commands.add(new LoadCommand());
        commands.add(new NewGameCommand());
        commands.add(new PrintCommand());
        commands.add(new HelpCommand(this));
        commands.add(new ExitCommand());
    }

    public List<Command> getAllCommands() {
        return Collections.unmodifiableList(commands);
    }

    public List<Command> getCommandsForPhase(GamePhase phase) {
        List<Command> result = new ArrayList<>();
        for (Command command : commands) {
            if (command.isAllowedIn(phase)) {
                result.add(command);
            }
        }
        return result;
    }

    /** Az első parancs, amelynek a mintájára a bemenet illik (fázistól függetlenül). */
    public Optional<MatchedCommand> find(String input) {
        for (Command command : commands) {
            Optional<Matcher> matcher = command.match(input);
            if (matcher.isPresent()) {
                return Optional.of(new MatchedCommand(command, matcher.get()));
            }
        }
        return Optional.empty();
    }

    /** Egy parancs és a bemenetre illesztett mintája. */
    public record MatchedCommand(Command command, Matcher matcher) {
    }
}
