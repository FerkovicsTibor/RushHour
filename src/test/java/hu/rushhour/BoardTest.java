package hu.rushhour;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(6, 12);
        board.addVehicle(new Vehicle('A', 2, 1, 2, true));
        board.addVehicle(new Vehicle('B', 3, 9, 2, true));
    }

    @Test
    void checksBoardDimensions() {
        assertEquals(6, board.getRows());
        assertEquals(12, board.getColumns());
    }

    @Test
    void rejectsInvalidDimensions() {
        assertThrows(IllegalArgumentException.class,
                () -> new Board(0, 12));

        assertThrows(IllegalArgumentException.class,
                () -> new Board(6, 0));
    }

    @Test
    void findsExistingVehicle() {
        assertNotNull(board.getVehicle('A'));
        assertNull(board.getVehicle('Z'));
    }

    @Test
    void checksBoardBoundaries() {
        assertTrue(board.isInside(0, 0));
        assertTrue(board.isInside(5, 11));
        assertFalse(board.isInside(-1, 0));
        assertFalse(board.isInside(0, -1));
        assertFalse(board.isInside(6, 0));
        assertFalse(board.isInside(0, 12));
    }

    @Test
    void checksEmptyCells() {
        assertTrue(board.isEmpty(0, 0));
        assertFalse(board.isEmpty(2, 1));
        assertFalse(board.isEmpty(2, 2));
    }

    @Test
    void movesVehicle() {
        assertTrue(board.applyMove(new Move('A', 2)));
        assertEquals(3, board.getVehicle('A').column());
    }

    @Test
    void rejectsUnknownVehicle() {
        assertFalse(board.canMove(new Move('Z', 1)));
        assertFalse(board.applyMove(new Move('Z', 1)));
    }

    @Test
    void rejectsBlockedMove() {
        board.addVehicle(new Vehicle('C', 0, 4, 3, false));

        assertFalse(board.canMove(new Move('A', 3)));
        assertFalse(board.applyMove(new Move('A', 3)));
    }

    @Test
    void rejectsMoveOutsideBoard() {
        assertFalse(board.canMove(new Move('A', -2)));
        assertFalse(board.canMove(new Move('B', 3)));
    }

    @Test
    void movesVerticalVehicle() {
        board.addVehicle(new Vehicle('C', 0, 4, 3, false));

        assertTrue(board.applyMove(new Move('C', 3)));
        assertEquals(3, board.getVehicle('C').row());
    }

    @Test
    void findsPossibleMoves() {
        assertFalse(board.getPossibleMoves('A').isEmpty());
        assertTrue(board.getPossibleMoves('Z').isEmpty());
    }

    @Test
    void playerWinsByExiting() {
        assertTrue(board.applyMove(new Move('A', 11)));
        assertTrue(board.isGameOver());
        assertEquals('A', board.getWinner());
    }

    @Test
    void computerWinsByExiting() {
        assertTrue(board.applyMove(new Move('B', -11)));
        assertTrue(board.isGameOver());
        assertEquals('B', board.getWinner());
    }

    @Test
    void rejectsMovesAfterGameOver() {
        board.applyMove(new Move('A', 11));

        assertFalse(board.canMove(new Move('B', -1)));
    }

    @Test
    void rejectsBlockedExit() {
        board.addVehicle(new Vehicle('C', 0, 4, 3, false));

        assertFalse(board.canMove(new Move('A', 11)));
        assertFalse(board.isGameOver());
    }

    @Test
    void returnsImmutableVehicleList() {
        assertThrows(UnsupportedOperationException.class,
                () -> board.getVehicles().clear());
    }
    @Test
    void rejectsDuplicateVehicle() {
        assertThrows(IllegalArgumentException.class,
                () -> board.addVehicle(
                        new Vehicle('A', 0, 0, 2, true)));
    }

    @Test
    void rejectsOverlappingVehicle() {
        assertThrows(IllegalArgumentException.class,
                () -> board.addVehicle(
                        new Vehicle('C', 2, 1, 2, true)));
    }

    @Test
    void rejectsVehicleOutsideBoard() {
        assertThrows(IllegalArgumentException.class,
                () -> board.addVehicle(
                        new Vehicle('C', 0, 11, 2, true)));
    }

    @Test
    void rejectsVerticalVehicleOutsideBoard() {
        assertThrows(IllegalArgumentException.class,
                () -> board.addVehicle(
                        new Vehicle('C', 5, 0, 2, false)));
    }

    @Test
    void movesVehicleLeft() {
        assertTrue(board.applyMove(new Move('A', -1)));
        assertEquals(0, board.getVehicle('A').column());
    }

    @Test
    void movesVerticalVehicleUp() {
        board.addVehicle(new Vehicle('C', 3, 4, 2, false));

        assertTrue(board.applyMove(new Move('C', -2)));
        assertEquals(1, board.getVehicle('C').row());
    }

    @Test
    void rejectsBlockedComputerExit() {
        board.addVehicle(new Vehicle('C', 2, 5, 3, false));

        assertFalse(board.canMove(new Move('B', -11)));
    }

    @Test
    void includesPlayerExitInPossibleMoves() {
        assertTrue(board.getPossibleMoves('A')
                .contains(new Move('A', 11)));
    }

    @Test
    void includesComputerExitInPossibleMoves() {
        assertTrue(board.getPossibleMoves('B')
                .contains(new Move('B', -11)));
    }

    @Test
    void printsBoard() {
        assertDoesNotThrow(() -> board.printBoard());
    }

    @Test
    void gameIsNotOverInitially() {
        assertFalse(board.isGameOver());
        assertEquals('\0', board.getWinner());
    }

    @Test
    void removesWinningVehicle() {
        board.applyMove(new Move('A', 11));

        assertNull(board.getVehicle('A'));
    }

}
