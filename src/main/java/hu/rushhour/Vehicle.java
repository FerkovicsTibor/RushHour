package hu.rushhour;

public record Vehicle(
        char id,
        int row,
        int column,
        int length,
        boolean horizontal
) {

    public Vehicle {
        if (length < 1) {
            throw new IllegalArgumentException("Érvénytelen járműhossz!");
        }

        if (row < 0 || column < 0) {
            throw new IllegalArgumentException("Érvénytelen pozíció!");
        }
    }

    public Vehicle move(int distance) {
        if (horizontal) {
            return new Vehicle(id, row, column + distance, length, horizontal);
        }

        return new Vehicle(id, row + distance, column, length, horizontal);
    }
}
