package hu.rushhour;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class ComputerPlayerTest {

    @Test
    void choosesValidMove() {
        Board board = new Board(6, 12);
        board.addVehicle(new Vehicle('A', 2, 1, 2, true));

        ComputerPlayer computer = new ComputerPlayer();
        Move move = computer.chooseMove(board);

        assertNotNull(move);
        assertTrue(board.canMove(move));
    }

    @Test
    void returnsNullForEmptyBoard() {
        Board board = new Board(6, 12);
        ComputerPlayer computer = new ComputerPlayer();

        assertNull(computer.chooseMove(board));
    }

    @Test
    void choosesOnlyAvailableMove() {
        Board board = mock(Board.class);

        Vehicle vehicle = new Vehicle('C', 0, 0, 2, true);
        Move expectedMove = new Move('C', 1);

        when(board.getVehicles()).thenReturn(List.of(vehicle));
        when(board.getPossibleMoves('C'))
                .thenReturn(List.of(expectedMove));

        ComputerPlayer computer = new ComputerPlayer();

        assertEquals(expectedMove, computer.chooseMove(board));

        verify(board).getVehicles();
        verify(board, atLeastOnce()).getPossibleMoves('C');
    }

    @Test
    void returnsNullWhenVehicleCannotMove() {
        Board board = mock(Board.class);

        Vehicle vehicle = new Vehicle('C', 0, 0, 2, true);

        when(board.getVehicles()).thenReturn(List.of(vehicle));
        when(board.getPossibleMoves('C')).thenReturn(List.of());

        ComputerPlayer computer = new ComputerPlayer();

        assertNull(computer.chooseMove(board));
    }
}
