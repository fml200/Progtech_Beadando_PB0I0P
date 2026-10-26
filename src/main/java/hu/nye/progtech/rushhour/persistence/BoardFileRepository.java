package hu.nye.progtech.rushhour.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.nye.progtech.rushhour.exception.BoardFileException;
import hu.nye.progtech.rushhour.model.Board;

/** Pályák betöltése szövegfájlból és állás mentése szövegfájlba. */
public class BoardFileRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardFileRepository.class);

    private final BoardTextReader reader = new BoardTextReader();
    private final BoardTextWriter writer = new BoardTextWriter();

    /**
     * Betölt egy pályát.
     *
     * @throws BoardFileException ha a fájl nem olvasható
     * @throws hu.nye.progtech.rushhour.exception.InvalidBoardException ha a tartalma nem érvényes pálya
     */
    public Board load(String fileName) {
        Path path = toPath(fileName);
        try {
            Board board = reader.parse(Files.readString(path, StandardCharsets.UTF_8));
            LOGGER.info("Pálya betöltve: {}", path);
            return board;
        } catch (IOException e) {
            throw new BoardFileException("A fájl nem olvasható: " + fileName, e);
        }
    }

    /**
     * Elmenti az állást.
     *
     * @throws BoardFileException ha a fájl nem írható
     */
    public void save(Board board, String fileName) {
        Path path = toPath(fileName);
        try {
            Files.writeString(path, writer.write(board), StandardCharsets.UTF_8);
            LOGGER.info("Állás elmentve: {}", path);
        } catch (IOException e) {
            throw new BoardFileException("A fájl nem írható: " + fileName, e);
        }
    }

    private Path toPath(String fileName) {
        try {
            return Path.of(fileName);
        } catch (InvalidPathException e) {
            throw new BoardFileException("Érvénytelen fájlnév: " + fileName, e);
        }
    }
}
