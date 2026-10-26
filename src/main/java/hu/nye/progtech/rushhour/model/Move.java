package hu.nye.progtech.rushhour.model;

import java.util.Objects;

/** Egy lépés: az adott járművet az adott irányba az adott számú mezővel csúsztatjuk. */
public record Move(char vehicleId, Direction direction, int steps) {

    public Move {
        Objects.requireNonNull(direction, "direction");
    }
}
