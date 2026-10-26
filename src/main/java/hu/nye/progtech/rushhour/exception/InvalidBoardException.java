package hu.nye.progtech.rushhour.exception;

/** Érvénytelen pálya (átfedő járművek, hiányzó céljármű, hibás pályafájl). */
public class InvalidBoardException extends GameException {

    private static final long serialVersionUID = 1L;

    public InvalidBoardException(String message) {
        super(message);
    }
}
