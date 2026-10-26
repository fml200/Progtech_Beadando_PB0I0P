package hu.nye.progtech.rushhour.ui.command;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Command minta: a felhasználó egy parancsa. Minden parancs megmondja, milyen szöveg
 * illik rá (reguláris kifejezés), melyik játékfázisban használható, és végrehajtja magát.
 */
public abstract class Command {

    private final String name;
    private final String usage;
    private final String description;
    private final Pattern pattern;
    private final Set<GamePhase> allowedPhases;

    protected Command(String name, String usage, String description, String regex,
                      Set<GamePhase> allowedPhases) {
        this.name = name;
        this.usage = usage;
        this.description = description;
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        this.allowedPhases = EnumSet.copyOf(allowedPhases);
    }

    /** A bemenethez tartozó illesztés, ha a szöveg erre a parancsra illik. */
    public Optional<Matcher> match(String input) {
        Matcher matcher = pattern.matcher(input.trim());
        return matcher.matches() ? Optional.of(matcher) : Optional.empty();
    }

    public boolean isAllowedIn(GamePhase phase) {
        return allowedPhases.contains(phase);
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Végrehajtja a parancsot.
     *
     * @param context a játék környezete
     * @param matcher a bemenet illesztése (a paraméterek a csoportokban)
     * @throws hu.nye.progtech.rushhour.exception.GameException ha a parancs nem hajtható végre
     */
    public abstract void execute(GameContext context, Matcher matcher);
}
