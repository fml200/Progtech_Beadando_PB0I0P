package hu.nye.progtech.rushhour.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Egy L hosszúságú, rögzített irányú jármű. A {@code start} a jármű bal szélső (vízszintes)
 * vagy legfelső (függőleges) mezője. Változtathatatlan value object.
 */
public record Vehicle(char id, Position start, int length, Orientation orientation) {

    public static final int MIN_LENGTH = 2;

    public Vehicle {
        if (id < 'A' || id > 'Z') {
            throw new IllegalArgumentException("A jármű azonosítója A és Z közötti betű lehet: " + id);
        }
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(orientation, "orientation");
        if (length < MIN_LENGTH) {
            throw new IllegalArgumentException("A jármű legalább " + MIN_LENGTH + " mező hosszú: " + length);
        }
    }

    /** A jármű által elfoglalt mezők a start mezőtől a vége felé haladva. */
    public List<Position> cells() {
        Direction forward = forwardDirection();
        List<Position> cells = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            cells.add(start.shifted(forward, i));
        }
        return Collections.unmodifiableList(cells);
    }

    /** A jármű utolsó (jobb szélső vagy legalsó) mezője. */
    public Position end() {
        return start.shifted(forwardDirection(), length - 1);
    }

    public boolean occupies(Position position) {
        return cells().contains(position);
    }

    /**
     * Az elmozgatott jármű. Az eredeti példány nem változik.
     *
     * @throws IllegalArgumentException ha az irány nem a jármű tengelyébe esik
     */
    public Vehicle moved(Direction direction, int steps) {
        if (direction.getOrientation() != orientation) {
            throw new IllegalArgumentException(
                    "A(z) " + id + " jármű nem mozdulhat ebbe az irányba: " + direction);
        }
        return new Vehicle(id, start.shifted(direction, steps), length, orientation);
    }

    private Direction forwardDirection() {
        return orientation == Orientation.HORIZONTAL ? Direction.RIGHT : Direction.DOWN;
    }
}
