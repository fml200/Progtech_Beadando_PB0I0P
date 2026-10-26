package hu.nye.progtech.rushhour.ui.command;

import java.io.PrintStream;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.persistence.BoardFileRepository;
import hu.nye.progtech.rushhour.service.GameSession;
import hu.nye.progtech.rushhour.service.MachinePlayer;
import hu.nye.progtech.rushhour.ui.BoardRenderer;

/** A parancsok környezete: a játszma, a kimenet, a fájlkezelő és a kilépési jelző. */
public class GameContext {

    private static final String DEFAULT_PLAYER_NAME = "Játékos";

    private final PrintStream out;
    private final BoardRenderer renderer;
    private final BoardFileRepository repository;
    private final Board initialBoard;
    private final MachinePlayer machinePlayer;
    private GameSession session;
    private String playerName = DEFAULT_PLAYER_NAME;
    private boolean exitRequested;

    public GameContext(PrintStream out, BoardRenderer renderer, BoardFileRepository repository,
                       Board initialBoard, MachinePlayer machinePlayer) {
        this.out = out;
        this.renderer = renderer;
        this.repository = repository;
        this.initialBoard = initialBoard;
        this.machinePlayer = machinePlayer;
        this.session = new GameSession(initialBoard, machinePlayer);
    }

    public PrintStream getOut() {
        return out;
    }

    public BoardFileRepository getRepository() {
        return repository;
    }

    public GameSession getSession() {
        return session;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public GamePhase getPhase() {
        return session.isFinished() ? GamePhase.FINISHED : GamePhase.PLAYING;
    }

    /** Új játszma indul a megadott táblával. */
    public void startSession(Board board) {
        session = new GameSession(board, machinePlayer);
    }

    /** Új játszma indul a kezdőpályával. */
    public void restart() {
        startSession(initialBoard);
    }

    public void printBoard() {
        out.println(renderer.render(session.getBoard()));
    }

    public boolean isExitRequested() {
        return exitRequested;
    }

    public void requestExit() {
        exitRequested = true;
    }
}
