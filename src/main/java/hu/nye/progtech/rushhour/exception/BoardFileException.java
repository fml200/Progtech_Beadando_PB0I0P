package hu.nye.progtech.rushhour.exception;

/** A pályafájl nem olvasható vagy nem írható. */
public class BoardFileException extends GameException {

    private static final long serialVersionUID = 1L;

    public BoardFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
