package mazie.model.monster;

import java.util.Random;

import mazie.exception.ModelException;

public abstract class Monster {

    final String name;
    final String[] actions;
    final String finalMessage;
    final int attack;
    final int defence;
    int hp;
    final int xpReward;

    private static final Random RANDOM = new Random();

    protected Monster(String name, String[] actions, String finalMessage, int heroLevel, int baseAttack, int baseDefence, int baseHp, int baseXpReward) {
        this.name = name;
        this.actions = actions;
        this.finalMessage = finalMessage;
        this.attack = calcAttack(baseAttack, heroLevel);
        this.defence = calcDefence(baseDefence, heroLevel);
        this.hp = calcHp(baseHp, heroLevel);
        this.xpReward = calcXpReward(baseXpReward, heroLevel);
    }

    public String getName() {
        return this.name;
    }

    public String getAction() {
        if (actions.length <= 0) {
            throw new ModelException("%s has no action".formatted(this.name));
        }
        return actions[RANDOM.nextInt(actions.length)];
    }

    public String getFinalMessage() {
        return this.finalMessage;
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

    protected static int calcAttack(int base, int lvl) {
        return base + (base * (lvl - 1)) / 50;
    }

    protected static int calcDefence(int base, int lvl) {
        return base + (base * (lvl - 1)) / 50;
    }

    protected static int calcHp(int base, int lvl) {
        return base + (base * (lvl - 1)) / 50;
    }

    protected static int calcXpReward(int base, int lvl) {
        return base + (base * (lvl - 1)) / 10;
    }

    @Override
    public String toString() {
        return String.format(
                """
            Monster: %s
             attack:%d, defence:%d, hp:%d
            """, this.name, this.attack, this.defence, this.hp
        );
    }
}
