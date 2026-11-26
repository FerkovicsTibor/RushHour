package hu.rushhour;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VehicleTest {

    @Test
    void createsValidVehicle() {
        Vehicle vehicle = new Vehicle('A', 2, 1, 2, true);

        assertEquals('A', vehicle.id());
        assertEquals(2, vehicle.row());
        assertEquals(1, vehicle.column());
        assertEquals(2, vehicle.length());
        assertTrue(vehicle.horizontal());
    }

    @Test
    void rejectsShortVehicle() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle('A', 0, 0, 1, true));
    }

    @Test
    void rejectsNegativeRow() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle('A', -1, 0, 2, true));
    }

    @Test
    void rejectsNegativeColumn() {
        assertThrows(IllegalArgumentException.class,
                () -> new Vehicle('A', 0, -1, 2, true));
    }

    @Test
    void movesHorizontalVehicle() {
        Vehicle vehicle = new Vehicle('A', 2, 1, 2, true);
        Vehicle moved = vehicle.move(3);

        assertEquals(2, moved.row());
        assertEquals(4, moved.column());
        assertEquals(1, vehicle.column());
    }

    @Test
    void movesVerticalVehicle() {
        Vehicle vehicle = new Vehicle('C', 1, 4, 3, false);
        Vehicle moved = vehicle.move(2);

        assertEquals(3, moved.row());
        assertEquals(4, moved.column());
        assertEquals(1, vehicle.row());
    }
}
