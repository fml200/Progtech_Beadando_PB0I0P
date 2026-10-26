package hu.nye.progtech.rushhour.ui.command;

import java.util.EnumSet;
import java.util.regex.Matcher;

/** new - új játék a kezdőpályáról. */
public class NewGameCommand extends Command {

    public NewGameCommand() {
        super("new", "new", "új játék a kezdőpályáról", "^new$", EnumSet.allOf(GamePhase.class));
    }

    @Override
    public void execute(GameContext context, Matcher matcher) {
        context.restart();
        context.getOut().println("Új játék indult.");
        context.printBoard();
    }
}
