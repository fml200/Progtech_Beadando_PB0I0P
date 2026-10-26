package hu.nye.progtech.rushhour.persistence;

import java.util.Optional;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Side;
import hu.nye.progtech.rushhour.model.Vehicle;

/** A {@link Board}-ot a {@link BoardTextReader} által olvasható szöveggé alakítja. */
public class BoardTextWriter {

    public String write(Board board) {
        StringBuilder text = new StringBuilder("# Rush Hour pálya\n");
        text.append(board.getRows()).append(' ').append(board.getColumns()).append('\n');
        for (Side side : Side.values()) {
            text.append(side.getHeroId()).append(' ').append(board.getExit(side).name()).append('\n');
        }
        for (int row = 0; row < board.getRows(); row++) {
            for (int column = 0; column < board.getColumns(); column++) {
                Optional<Vehicle> vehicle = board.getVehicleAt(new Position(row, column));
                text.append(vehicle.isPresent() ? vehicle.get().id() : '.');
            }
            text.append('\n');
        }
        return text.toString();
    }
}
