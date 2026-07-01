package mazie.model;

import java.util.Random;

public class Monster {

    private String name = "default";
    private int attack;
    private int defence;
    private int hp;
    private int xpReward;

    private static final Random RANDOM = new Random();
    private static final String[] EASY_NAMES = {"butterfly", "fish", "hamster"};
    private static final String[] MEDIUM_NAMES = {"cat", "mosquito", "cow", "seal"};
    private static final String[] HARD_NAMES = {"tiger", "shark", "capibara"};

    private Monster(String name, int attack, int defence, int hp, int xpReward) {
        this.name = name;
        this.attack = attack;
        this.defence = defence;
        this.hp = hp;
        this.xpReward = xpReward;
    }

    public static Monster easy(int heroLevel) {
        final var name = EASY_NAMES[RANDOM.nextInt(EASY_NAMES.length)];
        final var val = (heroLevel / 2) + heroLevel;
        final var attack = (val + 8) * 2;
        final var defence = (int) (val * 2.2);
        final var hp = val * 20;
        final var xp = val * 180;
        return new Monster("(easy) " + name, attack, defence, hp, xp);
    }

    public static Monster medium(int heroLevel) {
        String name = MEDIUM_NAMES[RANDOM.nextInt(MEDIUM_NAMES.length)];
        final var val = (heroLevel / 2) + heroLevel;
        final var attack = (val + 10) * 2;
        final var defence = (int) (val * 2.6);
        final var hp = val * 25;
        final var xp = val * 240;
        return new Monster("(medium) " + name, attack, defence, hp, xp);
    }

    public static Monster hard(int heroLevel) {
        String name = HARD_NAMES[RANDOM.nextInt(HARD_NAMES.length)];
        final var val = (heroLevel / 2) + heroLevel;
        final var attack = (val + 15) * 2;
        final var defence = (int) (val * 3.2);
        final var hp = val * 32;
        final var xp = val * 300;
        return new Monster("(hard) " + name, attack, defence, hp, xp);
    }

    public String getName() {
        return this.name;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getDefence() {
        return this.defence;
    }

    public int getHp() {
        return this.hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getXpReward() {
        return this.xpReward;
    }

    @Override
    public String toString() {
        return String.format(
                """
            Evil %s
             attack:%d, defence:%d, hp:%d
            """, this.name, this.attack, this.defence, this.hp
        );
    }

}
