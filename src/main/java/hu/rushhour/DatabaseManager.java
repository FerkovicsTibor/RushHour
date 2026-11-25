package hu.rushhour;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DATABASE_URL =
            "jdbc:sqlite:rushhour.db";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public void initialize() throws SQLException {
        String sql = """
        CREATE TABLE IF NOT EXISTS players (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          name TEXT NOT NULL UNIQUE,
          wins INTEGER NOT NULL DEFAULT 0
        )
        """;

        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);
        }
    }

    public void registerPlayer(String name) throws SQLException {
        String sql = """
        INSERT INTO players (name, wins)
        VALUES (?, 0)
        ON CONFLICT(name) DO NOTHING
        """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.executeUpdate();
        }
    }

    public void addWin(String name) throws SQLException {
        String sql = """
        UPDATE players
        SET wins = wins + 1
        WHERE name = ?
        """;

        try (Connection connection = connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.executeUpdate();
        }
    }
}
