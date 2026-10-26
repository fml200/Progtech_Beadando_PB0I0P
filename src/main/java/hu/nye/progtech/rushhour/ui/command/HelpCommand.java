package hu.nye.progtech.rushhour.ui.command;

import java.util.EnumSet;
import java.util.regex.Matcher;

/** help - a jelenleg használható parancsok listája. */
public class HelpCommand extends Command {

    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        super("help", "help", "a használható parancsok listája", "^help$", EnumSet.allOf(GamePhase.class));
        this.registry = registry;
    }

    @Override
    public void execute(GameContext context, Matcher matcher) {
        context.getOut().println("Használható parancsok:");
        for (Command command : registry.getCommandsForPhase(context.getPhase())) {
            context.getOut().println(String.format("  %-26s %s", command.getUsage(), command.getDescription()));
        }
    }
}
