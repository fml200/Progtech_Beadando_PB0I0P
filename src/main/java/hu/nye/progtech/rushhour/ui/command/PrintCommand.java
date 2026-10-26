package hu.nye.progtech.rushhour.ui.command;

import java.util.EnumSet;
import java.util.regex.Matcher;

/** print - a tábla kiírása. */
public class PrintCommand extends Command {

    public PrintCommand() {
        super("print", "print", "a tábla kiírása", "^print$", EnumSet.allOf(GamePhase.class));
    }

    @Override
    public void execute(GameContext context, Matcher matcher) {
        context.printBoard();
        context.getOut().println("Eddigi lépések száma: " + context.getSession().getMovesPlayed());
    }
}
