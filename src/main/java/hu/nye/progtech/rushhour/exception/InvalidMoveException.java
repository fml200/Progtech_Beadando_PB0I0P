package hu.nye.progtech.rushhour.exception;

/** Szabálytalan lépés (nincs ilyen jármű, rossz irány, útban álló jármű, vége a játéknak). */
public class InvalidMoveException extends GameException {

    private static final long serialVersionUID = 1L;

    public InvalidMoveException(String message) {
        super(message);
    }
}
