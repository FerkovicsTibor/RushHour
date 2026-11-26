package hu.rushhour;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(Game.class);

    private final Scanner scanner;
    private final ComputerPlayer computer;
    private final FileManager fileManager;
    private final DatabaseManager databaseManager;

    private String playerName;

    public Game() {
        this(new Scanner(System.in),
                new ComputerPlayer(),
                new FileManager(),
                new DatabaseManager());
    }

    public Game(Scanner scanner, ComputerPlayer computer,
                FileManager fileManager, DatabaseManager databaseManager) {

        this.scanner = scanner;
        this.computer = computer;
        this.fileManager = fileManager;
        this.databaseManager = databaseManager;
    }

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
            LOGGER.info("Bejelentkezett játékos: {}", playerName);
            return true;

        } catch (SQLException e) {
            LOGGER.error("Játékos regisztrációs hiba.", e);
            System.out.println("Adatbázishiba: " + e.getMessage());
            return false;
        }
    }

    private void showLeaderboard() {
        try {
            List<PlayerScore> scores = databaseManager.getLeaderboard();

            LOGGER.info("Ranglista lekérdezése.");

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
            LOGGER.error("Ranglista lekérdezési hiba.", e);
            System.out.println("Adatbázishiba: " + e.getMessage());
        }
    }

    private void loadAndPlay(boolean defaultBoard) {
        try {
            Board board;

            if (defaultBoard) {
                board = fileManager.loadDefaultBoard();
                LOGGER.info("Alapértelmezett pálya betöltve.");
            } else {
                System.out.print("Add meg a pályafájl elérési útját: ");
                String fileName = scanner.nextLine().trim();

                board = fileManager.loadBoard(fileName);
                LOGGER.info("Külső pálya betöltve: {}", fileName);
            }

            play(board, 'A');

        } catch (IOException | IllegalArgumentException e) {
            LOGGER.warn("Pályabetöltési hiba.", e);
            System.out.println("Betöltési hiba: " + e.getMessage());
        }
    }

    private void loadSavedGame() {
        System.out.print("Add meg a mentés nevét: ");
        String name = scanner.nextLine().trim();

        try {
            SavedGame savedGame = fileManager.loadGame(name);

            LOGGER.info("Mentett játék betöltve: {}", name);
            System.out.println("Játék sikeresen betöltve!");

            play(savedGame.board(), savedGame.currentPlayer());

        } catch (IOException | IllegalArgumentException e) {
            LOGGER.warn("Mentett játék betöltési hibája.", e);
            System.out.println("Betöltési hiba: " + e.getMessage());
        }
    }

    private void play(Board board, char currentPlayer) {
        LOGGER.info("Játék indítása. Következő játékos: {}",
                currentPlayer);

        while (!board.isGameOver()) {
            board.printBoard();

            if (!hasPossibleMoves(board)) {
                LOGGER.info("Nincs több szabályos lépés.");
                System.out.println("Nincs több szabályos lépés!");
                return;
            }

            if (currentPlayer == 'A') {
                System.out.println("Te következel!");

                if (!playerTurn(board)) {
                    LOGGER.info("A játékos visszatért a főmenübe.");
                    return;
                }

                currentPlayer = 'B';

            } else {
                System.out.println("A számítógép következik!");

                Move computerMove = computer.chooseMove(board);

                if (computerMove == null) {
                    LOGGER.info("A számítógép nem tud lépni.");
                    System.out.println("Nincs több szabályos lépés!");
                    return;
                }

                board.applyMove(computerMove);

                LOGGER.info("A számítógép lépése: {} {}",
                        computerMove.vehicleId(), computerMove.distance());

                System.out.println("A gép lépése: "
                        + computerMove.vehicleId() + " "
                        + computerMove.distance());

                currentPlayer = 'A';
            }
        }

        board.printBoard();

        if (board.getWinner() == 'A') {
            LOGGER.info("A játék győztese: {}", playerName);
            System.out.println("Gratulálok, " + playerName + ", nyertél!");
            recordWin();
        } else {
            LOGGER.info("A számítógép nyert.");
            System.out.println("A számítógép nyert!");
        }
    }

    private void recordWin() {
        try {
            databaseManager.addWin(playerName);

            LOGGER.info("Győzelem rögzítve: {}", playerName);
            System.out.println("A győzelmed bekerült az adatbázisba!");

        } catch (SQLException e) {
            LOGGER.error("Győzelem rögzítési hiba.", e);
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
                    LOGGER.info("Játékos lépése: {} {}", id, distance);
                    return true;
                }

                LOGGER.warn("Szabálytalan lépés: {} {}", id, distance);
                System.out.println("Szabálytalan lépés!");

            } catch (IllegalArgumentException e) {
                LOGGER.warn("Érvénytelen játékosbemenet.");
                System.out.println("Érvénytelen bemenet!");
            }
        }
    }

    private void saveGame(Board board, String name) {
        try {
            fileManager.saveGame(board, name, 'A');

            LOGGER.info("Játék elmentve: {}", name);

            System.out.println("Játék sikeresen elmentve: "
                    + name + ".txt");

        } catch (IOException | IllegalArgumentException e) {
            LOGGER.warn("Játékmentési hiba.", e);
            System.out.println("Mentési hiba: " + e.getMessage());
        }
    }
}
