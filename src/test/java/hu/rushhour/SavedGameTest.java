package hu.rushhour;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SavedGameTest {

    @Test
    void createsSavedGameWithHumanPlayer() {
        Board board = new Board(6, 12);
        SavedGame savedGame = new SavedGame(board, 'A');

        assertEquals(board, savedGame.board());
        assertEquals('A', savedGame.currentPlayer());
    }

    @Test
    void createsSavedGameWithComputerPlayer() {
        Board board = new Board(6, 12);
        SavedGame savedGame = new SavedGame(board, 'B');

        assertEquals('B', savedGame.currentPlayer());
    }

    @Test
    void rejectsNullBoard() {
        assertThrows(IllegalArgumentException.class,
                () -> new SavedGame(null, 'A'));
    }

    @Test
    void rejectsInvalidPlayer() {
        Board board = new Board(6, 12);

        assertThrows(IllegalArgumentException.class,
                () -> new SavedGame(board, 'X'));
    }
}
