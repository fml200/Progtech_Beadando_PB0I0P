package hu.nye.progtech.rushhour.ui.command;

import java.util.EnumSet;
import java.util.regex.Matcher;

/** save &lt;fájl&gt; - az aktuális állás mentése szövegfájlba. */
public class SaveCommand extends Command {

    public SaveCommand() {
        super("save", "save <fájl>", "az aktuális állás mentése szövegfájlba",
                "^save\\s+(\\S+)$", EnumSet.of(GamePhase.PLAYING));
    }

    @Override
    public void execute(GameContext context, Matcher matcher) {
        String fileName = matcher.group(1);
        context.getRepository().save(context.getSession().getBoard(), fileName);
        context.getOut().println("Állás elmentve: " + fileName);
    }
}
