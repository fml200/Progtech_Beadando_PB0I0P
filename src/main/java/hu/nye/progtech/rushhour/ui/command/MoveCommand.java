package hu.nye.progtech.rushhour.ui.command;

import java.util.EnumSet;
import java.util.Optional;
import java.util.regex.Matcher;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Move;
import hu.nye.progtech.rushhour.model.Side;
import hu.nye.progtech.rushhour.service.GameSession;

/**
 * move &lt;jármű&gt; &lt;irány&gt; &lt;mezők száma&gt; - a játékos lépése, majd rögtön a gép válasza.
 */
public class MoveCommand extends Command {

    private static final String REGEX =
            "^move\\s+([A-Za-z])\\s+(up|down|left|right|fel|le|balra|jobbra)\\s+(\\d+)$";

    public MoveCommand() {
        super("move", "move <jármű> <irány> <db>",
                "jármű csúsztatása (irány: up/down/left/right vagy fel/le/balra/jobbra), pl. move C down 2",
                REGEX, EnumSet.of(GamePhase.PLAYING));
    }

    @Override
    public void execute(GameContext context, Matcher matcher) {
        char vehicleId = Character.toUpperCase(matcher.group(1).charAt(0));
        Direction direction = Direction.fromText(matcher.group(2));
        Move humanMove = new Move(vehicleId, direction, parseSteps(matcher.group(3)));

        GameSession session = context.getSession();
        session.applyHumanMove(humanMove);
        context.getOut().println("Te: " + describe(humanMove));

        if (!session.isFinished()) {
            Optional<Move> machineMove = session.playMachineMove();
            if (machineMove.isPresent()) {
                context.getOut().println("Gép: " + describe(machineMove.get()));
            } else {
                context.getOut().println("A gép nem tud lépni, kimarad.");
            }
        }

        context.printBoard();
        announceWinner(context);
    }

    private int parseSteps(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new InvalidMoveException("Túl nagy lépésszám: " + text);
        }
    }

    private String describe(Move move) {
        return move.vehicleId() + " jármű " + move.direction().getLabel() + ", " + move.steps() + " mező";
    }

    private void announceWinner(GameContext context) {
        Optional<Side> winner = context.getSession().getWinner();
        if (winner.isEmpty()) {
            return;
        }
        if (winner.get() == Side.HUMAN) {
            context.getOut().println("Gratulálok, " + context.getPlayerName()
                    + "! Az A jármű elérte a kijáratot, nyertél!");
        } else {
            context.getOut().println("A gép nyert: a B jármű elérte a kijáratot. "
                    + "Indíts új játékot a 'new' paranccsal.");
        }
    }
}
