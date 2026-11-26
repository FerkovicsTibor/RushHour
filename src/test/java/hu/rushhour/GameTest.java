package hu.rushhour;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameTest {

    private ComputerPlayer computer;
    private FileManager fileManager;
    private DatabaseManager databaseManager;
    private Board board;

    @BeforeEach
    void setUp() {
        computer = mock(ComputerPlayer.class);
        fileManager = mock(FileManager.class);
        databaseManager = mock(DatabaseManager.class);

        board = new Board(6, 12);
        board.addVehicle(new Vehicle('A', 2, 1, 2, true));
        board.addVehicle(new Vehicle('B', 3, 9, 2, true));
    }

    private void runGame(String input) {
        try (Scanner scanner = new Scanner(input)) {
            Game game = new Game(
                    scanner, computer, fileManager, databaseManager);

            game.start();
        }
    }

    @Test
    void createsDefaultGame() {
        assertNotNull(new Game());
    }

    @Test
    void registersPlayerAndExits() throws SQLException {
        runGame("Player1\n5\n");

        verify(databaseManager).registerPlayer("Player1");
    }

    @Test
    void rejectsEmptyPlayerName() throws SQLException {
        runGame("\nPlayer1\n5\n");

        verify(databaseManager).registerPlayer("Player1");
    }

    @Test
    void handlesInvalidMenuChoice() throws SQLException {
        runGame("Player1\n9\n5\n");

        verify(databaseManager).registerPlayer("Player1");
    }

    @Test
    void handlesRegistrationError() throws SQLException {
        doThrow(new SQLException("Test error"))
                .when(databaseManager).registerPlayer("Player1");

        assertDoesNotThrow(() -> runGame("Player1\n"));
    }

    @Test
    void displaysLeaderboard() throws SQLException {
        when(databaseManager.getLeaderboard())
                .thenReturn(List.of(
                        new PlayerScore("Player1", 3),
                        new PlayerScore("Player2", 1)));

        runGame("Player1\n4\n5\n");

        verify(databaseManager).getLeaderboard();
    }

    @Test
    void displaysEmptyLeaderboard() throws SQLException {
        when(databaseManager.getLeaderboard())
                .thenReturn(List.of());

        runGame("Player1\n4\n5\n");

        verify(databaseManager).getLeaderboard();
    }

    @Test
    void handlesLeaderboardError() throws SQLException {
        when(databaseManager.getLeaderboard())
                .thenThrow(new SQLException("Test error"));

        assertDoesNotThrow(() -> runGame("Player1\n4\n5\n"));
    }

    @Test
    void startsDefaultGame() throws IOException {
        when(fileManager.loadDefaultBoard()).thenReturn(board);

        runGame("Player1\n1\nexit\n5\n");

        verify(fileManager).loadDefaultBoard();
    }

    @Test
    void loadsExternalBoard() throws IOException {
        when(fileManager.loadBoard("board.txt")).thenReturn(board);

        runGame("Player1\n2\nboard.txt\nexit\n5\n");

        verify(fileManager).loadBoard("board.txt");
    }

    @Test
    void handlesBoardLoadingError() throws IOException {
        when(fileManager.loadDefaultBoard())
                .thenThrow(new IOException("Test error"));

        assertDoesNotThrow(() -> runGame("Player1\n1\n5\n"));
    }

    @Test
    void loadsSavedGame() throws IOException {
        when(fileManager.loadGame("test"))
                .thenReturn(new SavedGame(board, 'A'));

        runGame("Player1\n3\ntest\nexit\n5\n");

        verify(fileManager).loadGame("test");
    }

    @Test
    void handlesSavedGameLoadingError() throws IOException {
        when(fileManager.loadGame("test"))
                .thenThrow(new IOException("Test error"));

        assertDoesNotThrow(
                () -> runGame("Player1\n3\ntest\n5\n"));
    }

    @Test
    void savesGame() throws IOException {
        when(fileManager.loadDefaultBoard()).thenReturn(board);

        runGame("Player1\n1\nsave test\nexit\n5\n");

        verify(fileManager).saveGame(board, "test", 'A');
    }

    @Test
    void handlesSaveError() throws IOException {
        when(fileManager.loadDefaultBoard()).thenReturn(board);

        doThrow(new IOException("Test error"))
                .when(fileManager).saveGame(board, "test", 'A');

        assertDoesNotThrow(
                () -> runGame("Player1\n1\nsave test\nexit\n5\n"));
    }

    @Test
    void rejectsInvalidSaveCommand() throws IOException {
        when(fileManager.loadDefaultBoard()).thenReturn(board);

        runGame("Player1\n1\nsave\nexit\n5\n");

        verify(fileManager, never())
                .saveGame(any(Board.class), anyString(), anyChar());
    }

    @Test
    void rejectsInvalidMoveInput() throws IOException {
        when(fileManager.loadDefaultBoard()).thenReturn(board);

        runGame("Player1\n1\ninvalid\nA x\nA 0\nexit\n5\n");

        assertEquals(1, board.getVehicle('A').column());
    }

    @Test
    void rejectsIllegalMove() throws IOException {
        when(fileManager.loadDefaultBoard()).thenReturn(board);

        runGame("Player1\n1\nA -10\nexit\n5\n");

        assertEquals(1, board.getVehicle('A').column());
    }

    @Test
    void recordsHumanVictory() throws IOException, SQLException {
        when(fileManager.loadDefaultBoard()).thenReturn(board);

        runGame("Player1\n1\nA 11\n5\n");

        assertEquals('A', board.getWinner());
        verify(databaseManager).addWin("Player1");
    }

    @Test
    void handlesWinRecordingError()
            throws IOException, SQLException {

        when(fileManager.loadDefaultBoard()).thenReturn(board);

        doThrow(new SQLException("Test error"))
                .when(databaseManager).addWin("Player1");

        assertDoesNotThrow(
                () -> runGame("Player1\n1\nA 11\n5\n"));

        assertEquals('A', board.getWinner());
    }

    @Test
    void handlesComputerVictory()
            throws IOException, SQLException {

        when(fileManager.loadDefaultBoard()).thenReturn(board);
        when(computer.chooseMove(board))
                .thenReturn(new Move('B', -11));

        runGame("Player1\n1\nA 1\n5\n");

        assertEquals('B', board.getWinner());
        verify(databaseManager, never()).addWin(anyString());
    }

    @Test
    void handlesComputerMove() throws IOException {
        when(fileManager.loadDefaultBoard()).thenReturn(board);
        when(computer.chooseMove(board))
                .thenReturn(new Move('B', -1));

        runGame("Player1\n1\nA 1\nexit\n5\n");

        assertEquals(2, board.getVehicle('A').column());
        assertEquals(8, board.getVehicle('B').column());
    }

    @Test
    void handlesMissingComputerMove() throws IOException {
        when(fileManager.loadDefaultBoard()).thenReturn(board);
        when(computer.chooseMove(board)).thenReturn(null);

        runGame("Player1\n1\nA 1\n5\n");

        verify(computer).chooseMove(board);
    }

    @Test
    void handlesBoardWithoutMoves() throws IOException {
        Board emptyBoard = new Board(6, 12);

        when(fileManager.loadDefaultBoard())
                .thenReturn(emptyBoard);

        runGame("Player1\n1\n5\n");

        verifyNoInteractions(computer);
    }

    @Test
    void resumesComputerTurn() throws IOException {
        when(fileManager.loadGame("test"))
                .thenReturn(new SavedGame(board, 'B'));

        when(computer.chooseMove(board))
                .thenReturn(new Move('B', -1));

        runGame("Player1\n3\ntest\nexit\n5\n");

        assertEquals(8, board.getVehicle('B').column());
        verify(computer).chooseMove(board);
    }
}
