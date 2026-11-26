package hu.rushhour;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileManagerTest {

    private final FileManager fileManager = new FileManager();

    @TempDir
    Path tempDirectory;

    @Test
    void loadsDefaultBoard() throws IOException {
        Board board = fileManager.loadDefaultBoard();

        assertEquals(6, board.getRows());
        assertEquals(12, board.getColumns());
        assertNotNull(board.getVehicle('A'));
        assertNotNull(board.getVehicle('B'));
    }

    @Test
    void parsesValidBoard() {
        Board board = fileManager.parseBoard(List.of(
                "6 12",
                "A 3 2 2 H",
                "B 4 10 2 H",
                "C 1 5 3 V"
        ));

        assertEquals(3, board.getVehicles().size());
        assertEquals(2, board.getVehicle('A').row());
        assertFalse(board.getVehicle('C').horizontal());
    }

    @Test
    void rejectsEmptyFile() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManager.parseBoard(List.of()));
    }

    @Test
    void rejectsInvalidDimensions() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManager.parseBoard(List.of("6")));
    }

    @Test
    void rejectsInvalidVehicleData() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManager.parseBoard(List.of(
                        "6 12",
                        "A 3 2"
                )));
    }

    @Test
    void rejectsInvalidDirection() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManager.parseBoard(List.of(
                        "6 12",
                        "A 3 2 2 X"
                )));
    }

    @Test
    void rejectsMissingTargetVehicles() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManager.parseBoard(List.of("6 12")));
    }

    @Test
    void loadsExternalFile() throws IOException {
        Path file = tempDirectory.resolve("board.txt");

        Files.write(file, List.of(
                "6 12",
                "A 3 2 2 H",
                "B 4 10 2 H"
        ));

        Board board = fileManager.loadBoard(file.toString());

        assertEquals(2, board.getVehicles().size());
    }

    @Test
    void rejectsMissingFile() {
        assertThrows(IOException.class,
                () -> fileManager.loadBoard(
                        tempDirectory.resolve("missing.txt").toString()));
    }

    @Test
    void rejectsInvalidSaveName() {
        Board board = new Board(6, 12);

        assertThrows(IllegalArgumentException.class,
                () -> fileManager.saveGame(board, "../test", 'A'));
    }

    @Test
    void rejectsInvalidCurrentPlayer() {
        Board board = new Board(6, 12);

        assertThrows(IllegalArgumentException.class,
                () -> fileManager.saveGame(board, "test", 'X'));
    }

    @Test
    void savesAndLoadsGame() throws IOException {
        Board board = fileManager.loadDefaultBoard();

        String name = "test_" + UUID.randomUUID();
        Path path = Path.of("saves", name + ".txt");

        try {
            fileManager.saveGame(board, name, 'A');

            assertTrue(Files.exists(path));

            SavedGame loaded = fileManager.loadGame(name);

            assertEquals('A', loaded.currentPlayer());
            assertEquals(board.getRows(), loaded.board().getRows());
            assertEquals(board.getColumns(), loaded.board().getColumns());
            assertEquals(board.getVehicles(), loaded.board().getVehicles());

        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void savesAndLoadsComputerTurn() throws IOException {
        Board board = fileManager.loadDefaultBoard();

        String name = "test_" + UUID.randomUUID();
        Path path = Path.of("saves", name + ".txt");

        try {
            fileManager.saveGame(board, name, 'B');

            SavedGame loaded = fileManager.loadGame(name);

            assertEquals('B', loaded.currentPlayer());

        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void rejectsInvalidLoadName() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManager.loadGame("../test"));
    }

    @Test
    void rejectsMissingSavedGame() {
        String name = "missing_" + UUID.randomUUID();

        assertThrows(IOException.class,
                () -> fileManager.loadGame(name));
    }

    @Test
    void ignoresEmptyLines() {
        Board board = fileManager.parseBoard(List.of(
                "6 12",
                "",
                "A 3 2 2 H",
                "",
                "B 4 10 2 H"
        ));

        assertEquals(2, board.getVehicles().size());
    }

    @Test
    void rejectsVerticalTargetVehicle() {
        assertThrows(IllegalArgumentException.class,
                () -> fileManager.parseBoard(List.of(
                        "6 12",
                        "A 1 1 2 V",
                        "B 4 10 2 H"
                )));
    }

    @Test
    void rejectsInvalidSavedTurn() throws IOException {
        String name = "test_" + UUID.randomUUID();

        Files.createDirectories(Path.of("saves"));
        Path path = Path.of("saves", name + ".txt");

        try {
            Files.write(path, List.of(
                    "TURN X",
                    "6 12",
                    "A 3 2 2 H",
                    "B 4 10 2 H"
            ));

            assertThrows(IllegalArgumentException.class,
                    () -> fileManager.loadGame(name));

        } finally {
            Files.deleteIfExists(path);
        }
    }

    @Test
    void rejectsIncompleteSavedGame() throws IOException {
        String name = "test_" + UUID.randomUUID();

        Files.createDirectories(Path.of("saves"));
        Path path = Path.of("saves", name + ".txt");

        try {
            Files.write(path, List.of("TURN A"));

            assertThrows(IllegalArgumentException.class,
                    () -> fileManager.loadGame(name));

        } finally {
            Files.deleteIfExists(path);
        }
    }
}
