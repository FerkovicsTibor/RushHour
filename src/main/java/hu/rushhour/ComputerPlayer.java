package hu.rushhour;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerPlayer {

    private final Random random = new Random();

    public Move chooseMove(Board board) {
        List<Vehicle> movableVehicles = new ArrayList<>();

        for (Vehicle vehicle : board.getVehicles()) {
            if (!board.getPossibleMoves(vehicle.id()).isEmpty()) {
                movableVehicles.add(vehicle);
            }
        }

        if (movableVehicles.isEmpty()) {
            return null;
        }

        Vehicle vehicle = movableVehicles.get(
                random.nextInt(movableVehicles.size()));

        List<Move> moves = board.getPossibleMoves(vehicle.id());

        return moves.get(random.nextInt(moves.size()));
    }
}
