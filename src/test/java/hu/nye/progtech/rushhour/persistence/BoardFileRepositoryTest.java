package hu.nye.progtech.rushhour.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import hu.nye.progtech.rushhour.TestBoards;
import hu.nye.progtech.rushhour.exception.BoardFileException;
import hu.nye.progtech.rushhour.exception.InvalidBoardException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.service.DefaultBoards;

class BoardFileRepositoryTest {

    private final BoardFileRepository repository = new BoardFileRepository();

    @TempDir
    Path directory;

    @Test
    void savedBoardCanBeLoadedAgain() {
        Path file = directory.resolve("save.txt");

        repository.save(TestBoards.simple(), file.toString());

        assertEquals(TestBoards.simple(), repository.load(file.toString()));
    }

    @Test
    void saveWritesTheTextFormat() throws IOException {
        Path file = directory.resolve("save.txt");

        repository.save(TestBoards.simple(), file.toString());

        assertTrue(Files.readString(file, StandardCharsets.UTF_8).contains("4 6\nA RIGHT\nB LEFT\n"));
    }

    @Test
    void loadFailsForAMissingFile() {
        String missing = directory.resolve("nincs.txt").toString();

        BoardFileException e = assertThrows(BoardFileException.class, () -> repository.load(missing));

        assertTrue(e.getMessage().contains("nem olvasható"));
    }

    @Test
    void loadFailsForInvalidContent() throws IOException {
        Path file = directory.resolve("bad.txt");
        Files.writeString(file, "ez nem pálya", StandardCharsets.UTF_8);

        assertThrows(InvalidBoardException.class, () -> repository.load(file.toString()));
    }

    @Test
    void saveFailsWhenTheDirectoryDoesNotExist() {
        String file = directory.resolve("nincs-ilyen-mappa").resolve("save.txt").toString();

        BoardFileException e = assertThrows(BoardFileException.class,
                () -> repository.save(TestBoards.simple(), file));

        assertTrue(e.getMessage().contains("nem írható"));
    }

    @Test
    void invalidFileNameIsReported() {
        BoardFileException e = assertThrows(BoardFileException.class, () -> repository.load("rossz\0nev"));

        assertTrue(e.getMessage().contains("Érvénytelen fájlnév"));
    }

    @Test
    void theBundledLevelFileMatchesTheBuiltInBoard() {
        Board fromFile = repository.load("level.txt");

        assertEquals(DefaultBoards.standard(), fromFile);
    }
}
