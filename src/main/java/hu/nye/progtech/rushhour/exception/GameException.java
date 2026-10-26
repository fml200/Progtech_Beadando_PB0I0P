package hu.nye.progtech.rushhour.exception;

/**
 * A játék által kiváltott, a felhasználónak szóló hibák közös őse.
 * A konzolos felület ezt fogja el, és írja ki a hibaüzenetet.
 */
public class GameException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
