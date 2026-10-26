package hu.nye.progtech.rushhour.ui.command;

import java.util.EnumSet;
import java.util.regex.Matcher;

import hu.nye.progtech.rushhour.model.Board;

/** load &lt;fájl&gt; - pálya vagy mentett állás betöltése, új játszma indul belőle. */
public class LoadCommand extends Command {

    public LoadCommand() {
        super("load", "load <fájl>", "pálya vagy mentett állás betöltése szövegfájlból",
                "^load\\s+(\\S+)$", EnumSet.allOf(GamePhase.class));
    }

    @Override
    public void execute(GameContext context, Matcher matcher) {
        String fileName = matcher.group(1);
        Board board = context.getRepository().load(fileName);
        context.startSession(board);
        context.getOut().println("Pálya betöltve: " + fileName);
        context.printBoard();
    }
}
