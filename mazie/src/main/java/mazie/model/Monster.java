package mazie.model;

import java.util.Random;

public class Monster {
    private String name = "default";
    private int attack = 2;
    private int defence = 2;
    private int hp = 100;
    private int xpReward = 100;

    private static final Random RANDOM = new Random();
    private static final String[] EASY_NAMES = {"butterfly", "fish", "hamster"};
    private static final String[] MEDIUM_NAMES = {"cat", "mosquito", "cow", "seal"};
    private static final String[] HARD_NAMES = {"tiger", "shark", "capibara"};

    private Monster() {};

    private Monster(String name) {
        this.name = name;
    }

    private Monster(String name, int attack, int defence, int hp, int xpReward) {
        this.name = name;
        this.attack = attack;
        this.defence = defence;
        this.hp = hp;
        this.xpReward = xpReward;
    }

    public static Monster easy(int heroLevel) {
        final var val = (heroLevel / 1) + 1;
        String name = EASY_NAMES[RANDOM.nextInt(EASY_NAMES.length)];
        return new Monster("(easy) " + name, val + 8, val, val * 10, val *80);
    }

    public static Monster medium(int heroLevel) {
        final var val = (heroLevel / 2) + 1;
        String name = MEDIUM_NAMES[RANDOM.nextInt(MEDIUM_NAMES.length)];
        return new Monster("(medium) " +name, val + 10, val, val * 10, val * 100);
    }

    public static Monster hard(int heroLevel) {
        final var val = (heroLevel) + 1;

        String name = HARD_NAMES[RANDOM.nextInt(HARD_NAMES.length)];
        return new Monster("(hard) " + name, val + 15, val, val * 10, val * 200);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAttack() {
        return this.attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefence() {
        return this.defence;
    }

    public void setDefence(int defence) {
        this.defence = defence;
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

    public void setXpReward(int xpReward) {
        this.xpReward = xpReward;
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
