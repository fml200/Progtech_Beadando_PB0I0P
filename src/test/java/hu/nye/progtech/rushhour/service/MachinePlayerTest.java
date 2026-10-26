package hu.nye.progtech.rushhour.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.Test;

import hu.nye.progtech.rushhour.StubRandom;
import hu.nye.progtech.rushhour.TestBoards;
import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Direction;
import hu.nye.progtech.rushhour.model.Move;

class MachinePlayerTest {

    @Test
    void choosesTheRandomVehicleAndItsRandomMove() {
        // A pályán az A, B és C jármű tud lépni (ebben a sorrendben): a 2. jármű (B) a választott,
        // lehetséges lépései: balra 1, 2, 3, 4 mező, ezekből a 3. (index 2)
        Random random = new StubRandom(1, 2);

        Optional<Move> move = new MachinePlayer(random).chooseMove(TestBoards.simple());

        assertEquals(Optional.of(new Move('B', Direction.LEFT, 3)), move);
    }

    @Test
    void skipsVehiclesThatCannotMove() {
        // Ezen a pályán a C jármű beszorult (fent az A, lent a tábla széle), csak az A és a B tud lépni.
        // A 2. mozgatható jármű (B) a választott, lehetséges lépései: balra 1, 2, 3 mező, ezekből a 3.
        Board board = TestBoards.simple().apply(new Move('C', Direction.DOWN, 2))
                .apply(new Move('A', Direction.LEFT, 1));
        Random random = new StubRandom(1, 2);

        Optional<Move> move = new MachinePlayer(random).chooseMove(board);

        assertEquals(Optional.of(new Move('B', Direction.LEFT, 3)), move);
    }

    @Test
    void alwaysChoosesALegalMove() {
        Board board = TestBoards.simple();

        for (int seed = 0; seed < 50; seed++) {
            Move move = new MachinePlayer(new Random(seed)).chooseMove(board).orElseThrow();

            assertTrue(board.getPossibleMoves().contains(move), "seed " + seed);
        }
    }

    @Test
    void returnsNothingWhenNoVehicleCanMove() {
        Optional<Move> move = new MachinePlayer(new Random(1)).chooseMove(TestBoards.deadlocked());

        assertTrue(move.isEmpty());
    }
}
