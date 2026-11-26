package hu.rushhour;

public record PlayerScore(String name, int wins) {

    public PlayerScore {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Érvénytelen játékosnév!");
        }

        if (wins < 0) {
            throw new IllegalArgumentException("Érvénytelen győzelemszám!");
        }
    }
}
