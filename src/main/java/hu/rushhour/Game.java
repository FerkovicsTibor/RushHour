package hu.rushhour;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Game {

    private final Scanner scanner = new Scanner(System.in);
    private final ComputerPlayer computer = new ComputerPlayer();
    private final FileManager fileManager = new FileManager();
    private final DatabaseManager databaseManager = new DatabaseManager();

    private String playerName;

    public void start() {
        System.out.println("Üdvözöllek a Rush Hour játékban!");

        if (!registerPlayer()) {
            return;
        }

        System.out.println("Szia, " + playerName + "!");

        while (true) {
            System.out.println("\n===== RUSH HOUR =====");
            System.out.println("1. Új játék");
            System.out.println("2. Pálya betöltése fájlból");
            System.out.println("3. Mentett játék folytatása");
            System.out.println("4. Ranglista");
            System.out.println("5. Kilépés");
            System.out.print("Válassz menüpontot: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> loadAndPlay(true);
                case "2" -> loadAndPlay(false);
                case "3" -> loadSavedGame();
                case "4" -> showLeaderboard();
                case "5" -> {
                    System.out.println("Viszlát!");
                    return;
                }
                default -> System.out.println("Érvénytelen menüpont!");
            }
        }
    }

    private boolean registerPlayer() {
        do {
            System.out.print("Add meg a játékosneved: ");
            playerName = scanner.nextLine().trim();

            if (playerName.isEmpty()) {
                System.out.println("A játékosnév nem lehet üres!");
            }

        } while (playerName.isEmpty());

        try {
            databaseManager.registerPlayer(playerName);
            return true;

        } catch (SQLException e) {
            System.out.println("Adatbázishiba: " + e.getMessage());
            return false;
        }
    }

    private void showLeaderboard() {
        try {
            List<PlayerScore> scores = databaseManager.getLeaderboard();

            System.out.println("\n===== RANGLISTA =====");

            if (scores.isEmpty()) {
                System.out.println("Még nincsenek játékosok!");
                return;
            }

            System.out.printf("%-8s %-20s %s%n",
                    "Hely", "Játékos", "Győzelmek");

            int position = 1;

            for (PlayerScore score : scores) {
                System.out.printf("%-8d %-20s %d%n",
                        position, score.name(), score.wins());

                position++;
            }

        } catch (SQLException e) {
            System.out.println("Adatbázishiba: " + e.getMessage());
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

            play(board, 'A');

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Betöltési hiba: " + e.getMessage());
        }
    }

    private void loadSavedGame() {
        System.out.print("Add meg a mentés nevét: ");
        String name = scanner.nextLine().trim();

        try {
            SavedGame savedGame = fileManager.loadGame(name);

            System.out.println("Játék sikeresen betöltve!");

            play(savedGame.board(), savedGame.currentPlayer());

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Betöltési hiba: " + e.getMessage());
        }
    }

    private void play(Board board, char currentPlayer) {
        while (!board.isGameOver()) {
            board.printBoard();

            if (!hasPossibleMoves(board)) {
                System.out.println("Nincs több szabályos lépés!");
                return;
            }

            if (currentPlayer == 'A') {
                System.out.println("Te következel!");

                if (!playerTurn(board)) {
                    return;
                }

                currentPlayer = 'B';

            } else {
                System.out.println("A számítógép következik!");

                Move computerMove = computer.chooseMove(board);

                if (computerMove == null) {
                    System.out.println("Nincs több szabályos lépés!");
                    return;
                }

                board.applyMove(computerMove);

                System.out.println("A gép lépése: "
                        + computerMove.vehicleId() + " "
                        + computerMove.distance());

                currentPlayer = 'A';
            }
        }

        board.printBoard();

        if (board.getWinner() == 'A') {
            System.out.println("Gratulálok, " + playerName + ", nyertél!");
            recordWin();
        } else {
            System.out.println("A számítógép nyert!");
        }
    }

    private void recordWin() {
        try {
            databaseManager.addWin(playerName);
            System.out.println("A győzelmed bekerült az adatbázisba!");

        } catch (SQLException e) {
            System.out.println("Adatbázishiba: " + e.getMessage());
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
            System.out.print(
                    "Add meg a lépésed (A 1, save név, exit): ");

            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("exit")) {
                return false;
            }

            String[] input = line.split("\\s+");

            if (input[0].equalsIgnoreCase("save")) {
                if (input.length != 2) {
                    System.out.println("Használat: save név");
                } else {
                    saveGame(board, input[1]);
                }

                continue;
            }

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

    private void saveGame(Board board, String name) {
        try {
            fileManager.saveGame(board, name, 'A');

            System.out.println("Játék sikeresen elmentve: "
                    + name + ".txt");

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Mentési hiba: " + e.getMessage());
        }
    }
}
