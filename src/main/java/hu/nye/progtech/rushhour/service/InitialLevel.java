package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;

/** A játék kezdőpályája és a betöltésről szóló tájékoztató üzenet. */
public record InitialLevel(Board board, String message) {
}
