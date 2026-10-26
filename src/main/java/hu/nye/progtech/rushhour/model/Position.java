package hu.nye.progtech.rushhour.model;

/** Egy mező helye a táblán (0-tól indexelve). Változtathatatlan value object. */
public record Position(int row, int column) {

    public Position shifted(Direction direction, int steps) {
        return new Position(row + direction.getRowDelta() * steps, column + direction.getColumnDelta() * steps);
    }
}
