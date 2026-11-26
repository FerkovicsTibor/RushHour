package hu.rushhour;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatabaseManagerTest {

    @TempDir
    Path tempDirectory;

    private DatabaseManager databaseManager;

    @BeforeEach
    void setUp() throws SQLException {
        String url = "jdbc:sqlite:"
                + tempDirectory.resolve("test.db").toAbsolutePath();

        databaseManager = new DatabaseManager(url);
        databaseManager.initialize();
    }

    @Test
    void connectsToDatabase() throws SQLException {
        try (Connection connection = databaseManager.connect()) {
            assertFalse(connection.isClosed());
        }
    }

    @Test
    void initializesDatabase() throws SQLException {
        assertDoesNotThrow(() -> databaseManager.initialize());
    }

    @Test
    void returnsEmptyLeaderboard() throws SQLException {
        assertTrue(databaseManager.getLeaderboard().isEmpty());
    }

    @Test
    void registersPlayer() throws SQLException {
        databaseManager.registerPlayer("Player1");

        List<PlayerScore> scores =
                databaseManager.getLeaderboard();

        assertEquals(1, scores.size());
        assertEquals("Player1", scores.get(0).name());
        assertEquals(0, scores.get(0).wins());
    }

    @Test
    void doesNotRegisterDuplicatePlayer() throws SQLException {
        databaseManager.registerPlayer("Player1");
        databaseManager.registerPlayer("Player1");

        assertEquals(1, databaseManager.getLeaderboard().size());
    }

    @Test
    void recordsWin() throws SQLException {
        databaseManager.registerPlayer("Player1");
        databaseManager.addWin("Player1");

        assertEquals(1,
                databaseManager.getLeaderboard().get(0).wins());
    }

    @Test
    void recordsMultipleWins() throws SQLException {
        databaseManager.registerPlayer("Player1");

        databaseManager.addWin("Player1");
        databaseManager.addWin("Player1");
        databaseManager.addWin("Player1");

        assertEquals(3,
                databaseManager.getLeaderboard().get(0).wins());
    }

    @Test
    void preservesWinsAfterRegistration() throws SQLException {
        databaseManager.registerPlayer("Player1");
        databaseManager.addWin("Player1");
        databaseManager.registerPlayer("Player1");

        assertEquals(1,
                databaseManager.getLeaderboard().get(0).wins());
    }

    @Test
    void sortsLeaderboardByWins() throws SQLException {
        databaseManager.registerPlayer("Anna");
        databaseManager.registerPlayer("Bela");

        databaseManager.addWin("Bela");
        databaseManager.addWin("Bela");
        databaseManager.addWin("Anna");

        List<PlayerScore> scores =
                databaseManager.getLeaderboard();

        assertEquals("Bela", scores.get(0).name());
        assertEquals(2, scores.get(0).wins());
        assertEquals("Anna", scores.get(1).name());
        assertEquals(1, scores.get(1).wins());
    }

    @Test
    void sortsEqualScoresByName() throws SQLException {
        databaseManager.registerPlayer("Zoltan");
        databaseManager.registerPlayer("Anna");

        List<PlayerScore> scores =
                databaseManager.getLeaderboard();

        assertEquals("Anna", scores.get(0).name());
        assertEquals("Zoltan", scores.get(1).name());
    }

    @Test
    void returnsImmutableLeaderboard() throws SQLException {
        databaseManager.registerPlayer("Player1");

        List<PlayerScore> scores =
                databaseManager.getLeaderboard();

        assertThrows(UnsupportedOperationException.class,
                () -> scores.clear());
    }

    @Test
    void createsDefaultDatabaseManager() {
        assertNotNull(new DatabaseManager());
    }
}
