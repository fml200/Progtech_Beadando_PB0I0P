package hu.nye.progtech.rushhour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.nye.progtech.rushhour.exception.GameException;
import hu.nye.progtech.rushhour.persistence.BoardFileRepository;
import hu.nye.progtech.rushhour.service.InitialLevel;
import hu.nye.progtech.rushhour.service.MachinePlayer;
import hu.nye.progtech.rushhour.ui.BoardRenderer;
import hu.nye.progtech.rushhour.ui.command.Command;
import hu.nye.progtech.rushhour.ui.command.CommandRegistry;
import hu.nye.progtech.rushhour.ui.command.CommandRegistry.MatchedCommand;
import hu.nye.progtech.rushhour.ui.command.GameContext;
import hu.nye.progtech.rushhour.ui.command.GamePhase;

/**
 * A parancssoros játék: név bekérése, majd a parancsok beolvasása és végrehajtása
 * a kilépésig (vagy a bemenet végéig). A bemenet és a kimenet kívülről érkezik,
 * ezért a teljes játékmenet tesztelhető.
 */
public class Game {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private final BufferedReader in;
    private final PrintStream out;
    private final InitialLevel level;
    private final GameContext context;
    private final CommandRegistry registry = new CommandRegistry();

    public Game(BufferedReader in, PrintStream out, InitialLevel level, Random random) {
        this.in = in;
        this.out = out;
        this.level = level;
        this.context = new GameContext(out, new BoardRenderer(), new BoardFileRepository(),
                level.board(), new MachinePlayer(random));
    }

    /** Elindítja a játékot, és visszatér, ha a játékos kilép. */
    public void start() {
        out.println("=== Rush Hour ===");
        out.println(level.message());
        out.println("A te járműved az A, a gépé a B. Bármelyik járművet csúsztathatod a tengelye mentén,");
        out.println("akár több mezőt is. Az nyer, akinek a járműve előbb eléri a kijáratot.");

        out.print("Add meg a neved: ");
        out.flush();
        String name = readLine();
        if (name == null) {
            out.println();
            return;
        }
        if (!name.isBlank()) {
            context.setPlayerName(name.trim());
        }
        LOGGER.info("Játék indul, játékos: {}", context.getPlayerName());
        out.println("Sok sikert, " + context.getPlayerName() + "! A 'help' parancs kiírja a parancsokat.");
        context.printBoard();

        while (!context.isExitRequested()) {
            out.print(prompt());
            out.flush();
            String line = readLine();
            if (line == null) {
                out.println();
                break;
            }
            if (!line.isBlank()) {
                process(line);
            }
        }
    }

    private void process(String line) {
        LOGGER.debug("Parancs: {}", line);
        Optional<MatchedCommand> found = registry.find(line);
        if (found.isEmpty()) {
            out.println("Ismeretlen parancs vagy hibás használat. A 'help' kiírja a használható parancsokat.");
            return;
        }
        Command command = found.get().command();
        if (!command.isAllowedIn(context.getPhase())) {
            out.println("A(z) '" + command.getName() + "' parancs most nem használható.");
            return;
        }
        try {
            command.execute(context, found.get().matcher());
        } catch (GameException e) {
            LOGGER.debug("A parancs hibával végződött: {}", e.getMessage());
            out.println("Hiba: " + e.getMessage());
        }
    }

    private String prompt() {
        String state = context.getPhase() == GamePhase.FINISHED ? " | vége" : "";
        return "[" + context.getPlayerName() + state + "] > ";
    }

    private String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
