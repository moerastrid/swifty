package mazie.model;

public enum HeroType {
    FROG(10, 10, 100),
    MOUSE(12, 9, 96),
    WEEVIL(8, 11, 112);

    public final int baseAttack;
    public final int baseDefence;
    public final int baseHp;

    HeroType(int baseAttack, int baseDefence, int baseHp) {
        this.baseAttack = baseAttack;
        this.baseDefence = baseDefence;
        this.baseHp = baseHp;
    }
}
