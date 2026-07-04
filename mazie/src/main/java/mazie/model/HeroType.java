package mazie.model;

public enum HeroType {
    FROG(10, 10, 100),
    HARE(15, 6, 90),
    BEAR(8, 12, 120);

    public final int baseAttack;
    public final int baseDefence;
    public final int baseHp;

    HeroType(int baseAttack, int baseDefence, int baseHp) {
        this.baseAttack = baseAttack;
        this.baseDefence = baseDefence;
        this.baseHp = baseHp;
    }
}
