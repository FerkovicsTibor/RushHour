package hu.rushhour;

import java.io.IOException;
import java.util.Scanner;

public class Game {

    private final Scanner scanner = new Scanner(System.in);
    private final ComputerPlayer computer = new ComputerPlayer();
    private final FileManager fileManager = new FileManager();

    public void start() {
        System.out.println("Üdvözöllek a Rush Hour játékban!");

        while (true) {
            System.out.println("\n===== RUSH HOUR =====");
            System.out.println("1. Új játék");
            System.out.println("2. Pálya betöltése fájlból");
            System.out.println("3. Kilépés");
            System.out.print("Válassz menüpontot: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> loadAndPlay(true);
                case "2" -> loadAndPlay(false);
                case "3" -> {
                    System.out.println("Viszlát!");
                    return;
                }
                default -> System.out.println("Érvénytelen menüpont!");
            }
        }
    }

    private void loadAndPlay(boolean defaultBoard) {
        try {
            Board board;

            if (defaultBoard) {
                board = fileManager.loadDefaultBoard();
            } else {
                System.out.print("Add meg a pályafájl elérési útját: ");
                String fileName = scanner.nextLine().trim();

                board = fileManager.loadBoard(fileName);
            }

            play(board);

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Betöltési hiba: " + e.getMessage());
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
}
