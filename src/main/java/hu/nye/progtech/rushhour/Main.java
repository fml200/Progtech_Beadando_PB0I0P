package hu.nye.progtech.rushhour;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import hu.nye.progtech.rushhour.persistence.BoardFileRepository;
import hu.nye.progtech.rushhour.service.InitialLevel;
import hu.nye.progtech.rushhour.service.LevelProvider;

/**
 * A program belépési pontja. Az első argumentum (opcionális) a kezdőpálya fájlja; ha nincs megadva,
 * a munkakönyvtárban lévő {@code level.txt} töltődik be, ennek hiányában az alapértelmezett pálya indul.
 */
public final class Main {

    private static final String DEFAULT_LEVEL_FILE = "level.txt";

    private Main() {
        // csak a belépési pont
    }

    public static void main(String[] args) {
        String levelFile = args.length > 0 ? args[0] : defaultLevelFile();
        InitialLevel level = new LevelProvider(new BoardFileRepository()).provide(levelFile);

        // A kimenet a JVM konzolkódolását használja (Windowson a konzol kódlapját), a bemenet ugyanezt.
        PrintStream out = System.out;
        Charset inputCharset = ConsoleEncoding.inputCharset(System.getProperties(), out.charset());
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, inputCharset));

        new Game(in, out, level, new Random()).start();
    }

    private static String defaultLevelFile() {
        return Files.exists(Path.of(DEFAULT_LEVEL_FILE)) ? DEFAULT_LEVEL_FILE : null;
    }
}
