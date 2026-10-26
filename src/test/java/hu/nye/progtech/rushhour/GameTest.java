package hu.nye.progtech.rushhour;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import hu.nye.progtech.rushhour.service.InitialLevel;

/**
 * A teljes parancssoros játékmenet tesztje: a bemenet egy szöveg, a kimenet rögzített. A gépi játékos
 * véletlenszám-generátora csonkolt (mindig 0-t ad), így a gép válasza mindig az első szabályos lépés
 * az első mozgatható járművel.
 */
class GameTest {

    @TempDir
    Path directory;

    private static int count(String text, String part) {
        return text.split(Pattern.quote(part), -1).length - 1;
    }

    private String run(String input) {
        return run(new BufferedReader(new StringReader(input)));
    }

    private String run(BufferedReader in) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        Random random = new StubRandom();

        new Game(in, out, new InitialLevel(TestBoards.simple(), "Teszt pálya."), random).start();

        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void greetsAsksTheNameAndPrintsTheBoard() {
        String output = run("Anna\nexit\n");

        assertTrue(output.contains("=== Rush Hour ==="), output);
        assertTrue(output.contains("Teszt pálya."), output);
        assertTrue(output.contains("Add meg a neved: "), output);
        assertTrue(output.contains("Sok sikert, Anna!"), output);
        assertTrue(output.contains("    a b c d e f"), output);
        assertTrue(output.contains("[Anna] > "), output);
    }

    @Test
    void blankNameFallsBackToTheDefault() {
        String output = run("   \nexit\n");

        assertTrue(output.contains("[Játékos] > "), output);
    }

    @Test
    void endOfInputAtTheNameQuestionEndsTheGame() {
        assertDoesNotThrow(() -> run(""));
    }

    @Test
    void endOfInputAtTheCommandPromptEndsTheGame() {
        String output = run("Anna\n");

        assertTrue(output.contains("[Anna] > "));
    }

    @Test
    void exitStopsReadingCommands() {
        String output = run("Anna\nexit\nprint\n");

        assertEquals(1, count(output, "Viszlát!"));
        assertFalse(output.contains("Eddigi lépések"));
    }

    @Test
    void blankLinesAreIgnored() {
        String output = run("Anna\n\n   \nexit\n");

        assertFalse(output.contains("Ismeretlen"));
        assertFalse(output.contains("Hiba"));
    }

    @Test
    void unknownAndMalformedCommandsAreReported() {
        String output = run("Anna\nfoo\nmove A\nexit\n");

        assertEquals(2, count(output, "Ismeretlen parancs vagy hibás használat."));
    }

    @Test
    void helpListsTheCommands() {
        String output = run("Anna\nhelp\nexit\n");

        assertTrue(output.contains("move <jármű> <irány> <db>"), output);
        assertTrue(output.contains("Használható parancsok:"), output);
    }

    @Test
    void printShowsTheBoardAndTheMoveCount() {
        String output = run("Anna\nprint\nexit\n");

        assertTrue(output.contains("Eddigi lépések száma: 0"), output);
    }

    @Test
    void moveIsAnsweredByTheMachineWhichMayMoveAnyVehicle() {
        String output = run("Anna\nmove A right 1\nprint\nexit\n");

        assertTrue(output.contains("Te: A jármű jobbra, 1 mező"), output);
        assertTrue(output.contains("Gép: A jármű balra, 1 mező"), output);
        assertTrue(output.contains("Eddigi lépések száma: 2"), output);
    }

    @Test
    void illegalMoveIsReportedAndTheGameContinues() {
        String output = run("Anna\nmove A left 1\nmove Z up 1\nmove A right 1\nexit\n");

        assertTrue(output.contains("Hiba: Az A jármű nem mozdulhat balra irányba."), output);
        assertTrue(output.contains("Hiba: Nincs ilyen jármű: Z"), output);
        assertTrue(output.contains("Te: A jármű jobbra, 1 mező"), output);
    }

    @Test
    void humanCanWinTheGame() {
        String output = run("Anna\nmove A right 3\nexit\n");

        assertTrue(output.contains("Gratulálok, Anna! Az A jármű elérte a kijáratot, nyertél!"), output);
        assertTrue(output.contains("[Anna | vége] > "), output);
    }

    @Test
    void movingAndSavingAreRefusedAfterTheGameIsOver() {
        String output = run("Anna\nmove A right 3\nmove C down 1\nsave x.txt\nexit\n");

        assertTrue(output.contains("A(z) 'move' parancs most nem használható."), output);
        assertTrue(output.contains("A(z) 'save' parancs most nem használható."), output);
    }

    @Test
    void newGameAfterTheEndStartsOver() {
        String output = run("Anna\nmove A right 3\nnew\nmove A right 1\nexit\n");

        assertTrue(output.contains("Új játék indult."), output);
        assertTrue(output.contains("Te: A jármű jobbra, 1 mező"), output);
        assertTrue(count(output, "[Anna] > ") >= 2, output);
    }

    @Test
    void theCurrentStateCanBeSavedAndLoadedAgain() throws IOException {
        Path file = directory.resolve("allas.txt");

        String output = run("Anna\nmove C down 1\nsave " + file + "\nnew\nload " + file + "\nprint\nexit\n");

        assertTrue(Files.readString(file, StandardCharsets.UTF_8).contains("C.AA.."));
        assertTrue(output.contains("Állás elmentve: " + file), output);
        assertTrue(output.contains("Pálya betöltve: " + file), output);
        assertTrue(output.contains("Eddigi lépések száma: 0"), output);
    }

    @Test
    void loadingAMissingFileIsReported() {
        String missing = directory.resolve("nincs.txt").toString();

        String output = run("Anna\nload " + missing + "\nexit\n");

        assertTrue(output.contains("Hiba: A fájl nem olvasható: " + missing), output);
    }

    @Test
    void loadingAnInvalidFileIsReported() throws IOException {
        Path file = directory.resolve("rossz.txt");
        Files.writeString(file, "ez nem pálya", StandardCharsets.UTF_8);

        String output = run("Anna\nload " + file + "\nexit\n");

        assertTrue(output.contains("Hiba: A pályaleírás hiányos"), output);
    }

    @Test
    void readErrorsAreNotSwallowed() {
        BufferedReader broken = new BufferedReader(new Reader() {
            @Override
            public int read(char[] buffer, int offset, int length) throws IOException {
                throw new IOException("olvasási hiba");
            }

            @Override
            public void close() {
                // nincs mit lezárni
            }
        });

        assertThrows(UncheckedIOException.class, () -> run(broken));
    }
}
