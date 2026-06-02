package mazie.model;

import java.util.Random;

public class Monster {
    private String name = "default";
    private int attack = 2;
    private int defence = 2;
    private int hp = 100;
    private int xpReward = 100;
    private static Random random = new Random();

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
        return new Monster("(easy) " + randomEasyName(), val + 8, val, val * 10, val * 100);
    }

    public static Monster medium(int heroLevel) {
        final var val = (heroLevel / 2) + 1;
        return new Monster("(medium) " + randomMediumName(), val + 10, val, val * 10, val * 100);
    }

    public static Monster hard(int heroLevel) {
        final var val = (heroLevel) + 1;
        return new Monster("(hard) " + randomHardName(), val + 15, val, val * 10, val * 200);
    }

    private static String randomEasyName() {
        String[] names = {"butterfly", "fish", "hamster"};
        // String[] names = {"bookstore", "garden", "birthday party", "coffee bistro", "home office"};
        return names[random.nextInt(0, names.length -1)];
    }

    private static String randomMediumName() {
        String[] names = {"cat", "mosquito", "cow"};
        // String[] names = {"supermarket", "public park", "club night", "cozy restaurant", "kantoortuin"};
        return names[random.nextInt(0, names.length -1)];
    }

    private static String randomHardName() {
        String[] names = {"tiger", "shark", "capibara"};
        // String[] names = {"black friday sale", "garden", "boiler room", "all you can eat buffet", "networking event"};
        return names[random.nextInt(0, names.length -1)];
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
