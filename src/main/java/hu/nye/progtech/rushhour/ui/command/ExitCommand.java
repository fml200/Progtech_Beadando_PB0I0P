package hu.nye.progtech.rushhour.ui.command;

import java.util.EnumSet;
import java.util.regex.Matcher;

/** exit - kilépés a játékból. */
public class ExitCommand extends Command {

    public ExitCommand() {
        super("exit", "exit", "kilépés", "^(exit|quit)$", EnumSet.allOf(GamePhase.class));
    }

    @Override
    public void execute(GameContext context, Matcher matcher) {
        context.requestExit();
        context.getOut().println("Viszlát!");
    }
}
