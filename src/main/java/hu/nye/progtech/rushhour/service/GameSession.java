package hu.nye.progtech.rushhour.service;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.nye.progtech.rushhour.exception.InvalidMoveException;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Move;
import hu.nye.progtech.rushhour.model.Side;

/**
 * Egy játszma állapota: az aktuális tábla, a soron lévő játékos és az esetleges győztes.
 * A játékos kezd; a lépéseket a tábla ellenőrzi.
 */
public class GameSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameSession.class);

    private final MachinePlayer machinePlayer;
    private Board board;
    private Side currentSide = Side.HUMAN;
    private Side winner;
    private int movesPlayed;

    public GameSession(Board board, MachinePlayer machinePlayer) {
        this.board = Objects.requireNonNull(board, "board");
        this.machinePlayer = Objects.requireNonNull(machinePlayer, "machinePlayer");
        this.winner = board.findWinner().orElse(null);
    }

    public Board getBoard() {
        return board;
    }

    public Side getCurrentSide() {
        return currentSide;
    }

    public Optional<Side> getWinner() {
        return Optional.ofNullable(winner);
    }

    public boolean isFinished() {
        return winner != null;
    }

    public int getMovesPlayed() {
        return movesPlayed;
    }

    /**
     * A játékos lépése.
     *
     * @throws InvalidMoveException ha a lépés szabálytalan, vagy nem a játékos következik
     */
    public void applyHumanMove(Move move) {
        play(Side.HUMAN, move);
    }

    /**
     * A gép lépése. Ha a gép egyetlen járművet sem tud mozdítani, kimarad, és a játékos jön.
     *
     * @return a végrehajtott lépés, vagy üres, ha a gép kimaradt
     * @throws InvalidMoveException ha vége a játéknak, vagy nem a gép következik
     */
    public Optional<Move> playMachineMove() {
        requireTurn(Side.MACHINE);
        Optional<Move> move = machinePlayer.chooseMove(board);
        if (move.isPresent()) {
            play(Side.MACHINE, move.get());
        } else {
            LOGGER.info("A gép nem tud lépni, kimarad.");
            currentSide = Side.HUMAN;
        }
        return move;
    }

    private void play(Side side, Move move) {
        requireTurn(side);
        board = board.apply(move);
        movesPlayed++;
        winner = board.findWinner().orElse(null);
        LOGGER.info("{} lépett: {}", side.getLabel(), move);
        if (winner == null) {
            currentSide = side.opponent();
        } else {
            LOGGER.info("A játék véget ért, nyertes: {}", winner.getLabel());
        }
    }

    private void requireTurn(Side side) {
        if (isFinished()) {
            throw new InvalidMoveException("A játék véget ért. Indíts újat a 'new' paranccsal.");
        }
        if (currentSide != side) {
            throw new InvalidMoveException("Most nem a(z) " + side.getLabel().toLowerCase() + " következik.");
        }
    }
}
