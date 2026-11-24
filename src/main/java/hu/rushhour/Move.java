package hu.rushhour;

public record Move(char vehicleId, int distance) {

    public Move {
        if (distance == 0) {
            throw new IllegalArgumentException("A lépés nem lehet nulla!");
        }
    }
}
