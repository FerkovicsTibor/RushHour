package hu.rushhour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public Board loadDefaultBoard() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/board.txt");

        if (stream == null) {
            throw new IOException("Nem található az alapértelmezett pályafájl!");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            return parseBoard(reader.lines().toList());
        }
    }

    public Board loadBoard(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(fileName));

        return parseBoard(lines);
    }

    public Board parseBoard(List<String> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("A pályafájl üres!");
        }

        String[] dimensions = lines.get(0).trim().split("\\s+");

        if (dimensions.length != 2) {
            throw new IllegalArgumentException("Érvénytelen táblaméret!");
        }

        int rows = Integer.parseInt(dimensions[0]);
        int columns = Integer.parseInt(dimensions[1]);

        Board board = new Board(rows, columns);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] data = line.split("\\s+");

            if (data.length != 5 || data[0].length() != 1) {
                throw new IllegalArgumentException("Érvénytelen járműadat!");
            }

            char id = Character.toUpperCase(data[0].charAt(0));
            int row = Integer.parseInt(data[1]) - 1;
            int column = Integer.parseInt(data[2]) - 1;
            int length = Integer.parseInt(data[3]);

            if (!data[4].equalsIgnoreCase("H")
                    && !data[4].equalsIgnoreCase("V")) {
                throw new IllegalArgumentException("Érvénytelen irány!");
            }

            boolean horizontal = data[4].equalsIgnoreCase("H");

            board.addVehicle(
                    new Vehicle(id, row, column, length, horizontal));
        }

        Vehicle player = board.getVehicle('A');
        Vehicle computer = board.getVehicle('B');

        if (player == null || computer == null
                || !player.horizontal() || !computer.horizontal()) {
            throw new IllegalArgumentException(
                    "Hiányzó vagy érvénytelen céljárművek!");
        }

        return board;
    }

    public void saveGame(Board board, String name, char currentPlayer)
            throws IOException {

        validateSaveName(name);

        if (currentPlayer != 'A' && currentPlayer != 'B') {
            throw new IllegalArgumentException("Érvénytelen játékos!");
        }

        Path directory = Path.of("saves");
        Files.createDirectories(directory);

        List<String> lines = new ArrayList<>();

        lines.add("TURN " + currentPlayer);
        lines.add(board.getRows() + " " + board.getColumns());

        for (Vehicle vehicle : board.getVehicles()) {
            lines.add(vehicle.id() + " "
                    + (vehicle.row() + 1) + " "
                    + (vehicle.column() + 1) + " "
                    + vehicle.length() + " "
                    + (vehicle.horizontal() ? "H" : "V"));
        }

        Files.write(directory.resolve(name + ".txt"), lines);
    }

    public SavedGame loadGame(String name) throws IOException {
        validateSaveName(name);

        Path path = Path.of("saves", name + ".txt");
        List<String> lines = Files.readAllLines(path);

        if (lines.size() < 2) {
            throw new IllegalArgumentException("Érvénytelen mentési fájl!");
        }

        String[] turn = lines.get(0).trim().split("\\s+");

        if (turn.length != 2 || !turn[0].equals("TURN")
                || (!turn[1].equals("A") && !turn[1].equals("B"))) {
            throw new IllegalArgumentException("Érvénytelen játékossorrend!");
        }

        char currentPlayer = turn[1].charAt(0);
        Board board = parseBoard(lines.subList(1, lines.size()));

        return new SavedGame(board, currentPlayer);
    }

    private void validateSaveName(String name) {
        if (!name.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException("Érvénytelen mentési név!");
        }
    }
}
