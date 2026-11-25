package hu.rushhour;

public record SavedGame(Board board, char currentPlayer) {

    public SavedGame {
        if (board == null) {
            throw new IllegalArgumentException("Hiányzó játéktábla!");
        }

        if (currentPlayer != 'A' && currentPlayer != 'B') {
            throw new IllegalArgumentException("Érvénytelen játékos!");
        }
    }
}
