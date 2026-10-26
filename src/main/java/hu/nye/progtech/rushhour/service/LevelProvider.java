package hu.nye.progtech.rushhour.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.nye.progtech.rushhour.exception.GameException;
import hu.nye.progtech.rushhour.persistence.BoardFileRepository;

/**
 * Előállítja a kezdőpályát: a megadott fájlból, ennek hiányában vagy hibája esetén
 * a beégetett alapértelmezett pályát adja.
 */
public class LevelProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(LevelProvider.class);

    private final BoardFileRepository repository;

    public LevelProvider(BoardFileRepository repository) {
        this.repository = repository;
    }

    public InitialLevel provide(String fileName) {
        if (fileName == null) {
            return new InitialLevel(DefaultBoards.standard(), "Alapértelmezett pálya indul.");
        }
        try {
            return new InitialLevel(repository.load(fileName), "Pálya betöltve: " + fileName);
        } catch (GameException e) {
            LOGGER.warn("A pálya nem tölthető be ({}): {}", fileName, e.getMessage());
            return new InitialLevel(DefaultBoards.standard(), "A(z) '" + fileName
                    + "' pálya nem tölthető be (" + e.getMessage() + "), az alapértelmezett pálya indul.");
        }
    }
}
