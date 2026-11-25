package hu.rushhour;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();

        try {
            databaseManager.initialize();
        } catch (SQLException e) {
            System.err.println("Adatbázishiba: " + e.getMessage());
            return;
        }

        Game game = new Game();
        game.start();
    }
}
