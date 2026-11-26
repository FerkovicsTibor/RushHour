package hu.rushhour;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PlayerScoreTest {

    @Test
    void createsValidPlayerScore() {
        PlayerScore score = new PlayerScore("TestPlayer", 5);

        assertEquals("TestPlayer", score.name());
        assertEquals(5, score.wins());
    }

    @Test
    void acceptsZeroWins() {
        PlayerScore score = new PlayerScore("TestPlayer", 0);

        assertEquals(0, score.wins());
    }

    @Test
    void rejectsNullName() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlayerScore(null, 0));
    }

    @Test
    void rejectsEmptyName() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlayerScore("", 0));
    }

    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlayerScore("   ", 0));
    }

    @Test
    void rejectsNegativeWins() {
        assertThrows(IllegalArgumentException.class,
                () -> new PlayerScore("TestPlayer", -1));
    }
}
