package hu.nye.progtech.rushhour.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Move;
import hu.nye.progtech.rushhour.model.Vehicle;

/**
 * A gépi játékos: véletlenszerűen választ egy járművet, amelynek van lehetséges lépése,
 * és annak egy véletlen lépését hajtja végre.
 */
public class MachinePlayer {

    private final Random random;

    public MachinePlayer(Random random) {
        this.random = random;
    }

    /** A választott lépés, vagy üres, ha egyetlen jármű sem tud lépni. */
    public Optional<Move> chooseMove(Board board) {
        List<List<Move>> movesByVehicle = new ArrayList<>();
        for (Vehicle vehicle : board.getVehicles()) {
            List<Move> moves = board.getPossibleMoves(vehicle.id());
            if (!moves.isEmpty()) {
                movesByVehicle.add(moves);
            }
        }
        if (movesByVehicle.isEmpty()) {
            return Optional.empty();
        }
        List<Move> chosenVehicleMoves = movesByVehicle.get(random.nextInt(movesByVehicle.size()));
        return Optional.of(chosenVehicleMoves.get(random.nextInt(chosenVehicleMoves.size())));
    }
}
