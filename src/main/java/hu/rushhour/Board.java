package hu.rushhour;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private final int rows;
    private final int columns;
    private final List<Vehicle> vehicles;

    public Board(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Érvénytelen táblaméret!");
        }

        this.rows = rows;
        this.columns = columns;
        this.vehicles = new ArrayList<>();
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public List<Vehicle> getVehicles() {
        return List.copyOf(vehicles);
    }

    public Vehicle getVehicle(char id) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.id() == id) {
                return vehicle;
            }
        }

        return null;
    }

    public void addVehicle(Vehicle vehicle) {
        if (getVehicle(vehicle.id()) != null) {
            throw new IllegalArgumentException("Már létezik ilyen jármű!");
        }

        for (int i = 0; i < vehicle.length(); i++) {
            int row = vehicle.row() + (vehicle.horizontal() ? 0 : i);
            int column = vehicle.column() + (vehicle.horizontal() ? i : 0);

            if (!isInside(row, column) || !isEmpty(row, column)) {
                throw new IllegalArgumentException("Érvénytelen járműelhelyezés!");
            }
        }

        vehicles.add(vehicle);
    }

    public boolean isInside(int row, int column) {
        return row >= 0 && row < rows
                && column >= 0 && column < columns;
    }

    public boolean isEmpty(int row, int column) {
        for (Vehicle vehicle : vehicles) {
            for (int i = 0; i < vehicle.length(); i++) {
                int r = vehicle.row() + (vehicle.horizontal() ? 0 : i);
                int c = vehicle.column() + (vehicle.horizontal() ? i : 0);

                if (r == row && c == column) {
                    return false;
                }
            }
        }

        return true;
    }

    public void printBoard() {
        char[][] grid = new char[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                grid[row][column] = '.';
            }
        }

        for (Vehicle vehicle : vehicles) {
            for (int i = 0; i < vehicle.length(); i++) {
                int row = vehicle.row() + (vehicle.horizontal() ? 0 : i);
                int column = vehicle.column() + (vehicle.horizontal() ? i : 0);

                grid[row][column] = vehicle.id();
            }
        }

        System.out.printf("%4s", "");

        for (int column = 0; column < columns; column++) {
            System.out.printf("%4s", column + 1);
        }

        System.out.println();

        for (int row = 0; row < rows; row++) {
            System.out.printf("%4d", row + 1);

            for (int column = 0; column < columns; column++) {
                System.out.printf("%4c", grid[row][column]);
            }

            System.out.println();
        }
    }
}
