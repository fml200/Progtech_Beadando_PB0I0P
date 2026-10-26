package hu.nye.progtech.rushhour.ui.command;

import static org.mockito.Mockito.mock;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import hu.nye.progtech.rushhour.TestBoards;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.persistence.BoardFileRepository;
import hu.nye.progtech.rushhour.service.MachinePlayer;
import hu.nye.progtech.rushhour.ui.BoardRenderer;

/** Közös tesztkörnyezet a parancsok teszteléséhez: rögzített kimenet, mockolt fájlkezelő és gépi játékos. */
final class CommandFixture {

    final BoardFileRepository repository = mock(BoardFileRepository.class);
    final MachinePlayer machine = mock(MachinePlayer.class);
    final GameContext context;

    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    CommandFixture() {
        this(TestBoards.simple());
    }

    CommandFixture(Board board) {
        PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
        context = new GameContext(out, new BoardRenderer(), repository, board, machine);
    }

    String output() {
        return bytes.toString(StandardCharsets.UTF_8);
    }

    void run(Command command, String input) {
        command.execute(context, command.match(input).orElseThrow());
    }
}
