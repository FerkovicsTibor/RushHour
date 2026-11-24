package hu.rushhour;

import java.util.Scanner;

public class Game {

    private final Scanner scanner = new Scanner(System.in);
    private final ComputerPlayer computer = new ComputerPlayer();

    public void start() {
        System.out.println("Üdvözöllek a Rush Hour játékban!");

        while (true) {
            System.out.println("\n===== RUSH HOUR =====");
            System.out.println("1. Új játék");
            System.out.println("2. Kilépés");
            System.out.print("Válassz menüpontot: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> play(createDefaultBoard());
                case "2" -> {
                    System.out.println("Viszlát!");
                    return;
                }
                default -> System.out.println("Érvénytelen menüpont!");
            }
        }
    }

    private void play(Board board) {
        while (!board.isGameOver()) {
            board.printBoard();

            if (!hasPossibleMoves(board)) {
                System.out.println("Nincs több szabályos lépés!");
                return;
            }

            if (!playerTurn(board)) {
                return;
            }

            if (board.isGameOver()) {
                break;
            }

            Move computerMove = computer.chooseMove(board);

            if (computerMove == null) {
                System.out.println("Nincs több szabályos lépés!");
                return;
            }

            board.applyMove(computerMove);

            System.out.println("A gép lépése: "
                    + computerMove.vehicleId() + " "
                    + computerMove.distance());
        }

        board.printBoard();

        if (board.getWinner() == 'A') {
            System.out.println("Gratulálok, nyertél!");
        } else {
            System.out.println("A számítógép nyert!");
        }
    }

    private boolean hasPossibleMoves(Board board) {
        for (Vehicle vehicle : board.getVehicles()) {
            if (!board.getPossibleMoves(vehicle.id()).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private boolean playerTurn(Board board) {
        while (true) {
            System.out.print("Add meg a lépésed (pl. A 1, vagy exit): ");

            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("exit")) {
                return false;
            }

            String[] input = line.split("\\s+");

            try {
                if (input.length != 2 || input[0].length() != 1) {
                    throw new IllegalArgumentException();
                }

                char id = Character.toUpperCase(input[0].charAt(0));
                int distance = Integer.parseInt(input[1]);

                if (board.applyMove(new Move(id, distance))) {
                    return true;
                }

                System.out.println("Szabálytalan lépés!");
            } catch (IllegalArgumentException e) {
                System.out.println("Érvénytelen bemenet!");
            }
        }
    }

    private Board createDefaultBoard() {
        Board board = new Board(6, 12);

        board.addVehicle(new Vehicle('A', 2, 1, 2, true));
        board.addVehicle(new Vehicle('B', 3, 9, 2, true));
        board.addVehicle(new Vehicle('C', 0, 4, 3, false));
        board.addVehicle(new Vehicle('D', 2, 7, 3, false));
        board.addVehicle(new Vehicle('E', 0, 8, 2, true));
        board.addVehicle(new Vehicle('F', 3, 2, 3, false));
        board.addVehicle(new Vehicle('G', 5, 5, 3, true));
        board.addVehicle(new Vehicle('H', 0, 0, 2, false));

        return board;
    }
}
