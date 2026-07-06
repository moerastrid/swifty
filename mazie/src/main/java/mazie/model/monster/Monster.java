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

    protected Monster(String name, String[] actions, String finalMessage, int attack, int defence, int hp, int xpReward) {
        this.name = name;
        this.actions = actions;
        this.finalMessage = finalMessage;
        this.attack = attack;
        this.defence = defence;
        this.hp = hp;
        this.xpReward = xpReward;
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

    protected static int calcAttack(int lvl) {
        return 17 + (lvl * 12) / 10;
    }

    protected static int calcDefence(int lvl) {
        return 2 + (lvl * 8) / 10;
    }

    protected static int calcHp(int lvl) {
        return 20 + lvl * 6;
    }

    protected static int calcXp(int lvl) {
        return 95 + (lvl / 2 + lvl) * 125;
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
