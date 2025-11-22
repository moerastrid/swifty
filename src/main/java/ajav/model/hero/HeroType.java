package ajav.model.hero;

public enum HeroType {
    PENGUIN,
    FROG,
    BEAR,
    HARE,
    TURTLE;

    @Override
    public String toString() {
        return switch(this) {
            case PENGUIN -> "🐧";
            case FROG -> "🐸";
            case BEAR -> "🐻";
            case HARE -> "🐰";
            case TURTLE -> "🐢";
        };
    }
}
