package hu.rushhour;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("A Rush Hour program elindult.");

        DatabaseManager databaseManager = new DatabaseManager();

        try {
            databaseManager.initialize();
            LOGGER.info("Az adatbázis inicializálása sikeres.");
        } catch (SQLException e) {
            LOGGER.error("Adatbázis inicializálási hiba.", e);
            System.err.println("Adatbázishiba: " + e.getMessage());
            return;
        }

        Game game = new Game();
        game.start();

        LOGGER.info("A Rush Hour program leállt.");
    }
}
