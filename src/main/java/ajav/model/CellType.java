package ajav.model;

public enum CellType {
    EMPTY,
    WALL,
    HERO,
    MONSTER_EASY,
    MONSTER_MEDIUM,
    MONSTER_HARD,
    ARTIFACT;

    @Override
    public String toString() {
        return switch (this) {
            case EMPTY -> ".";
            case WALL -> "#";
            case HERO -> "H";
            case MONSTER_EASY -> "m";
            case MONSTER_MEDIUM -> "M";
            case MONSTER_HARD -> "W";
            case ARTIFACT -> "A";
        };
    }
}
