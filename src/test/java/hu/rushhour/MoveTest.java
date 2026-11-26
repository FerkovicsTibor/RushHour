package hu.rushhour;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MoveTest {

    @Test
    void createsPositiveMove() {
        Move move = new Move('A', 3);

        assertEquals('A', move.vehicleId());
        assertEquals(3, move.distance());
    }

    @Test
    void createsNegativeMove() {
        Move move = new Move('B', -2);

        assertEquals(-2, move.distance());
    }

    @Test
    void rejectsZeroDistance() {
        assertThrows(IllegalArgumentException.class,
                () -> new Move('A', 0));
    }
}
