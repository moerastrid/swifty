package mazie.model.monster;

import mazie.exception.ModelException;

import java.util.Random;
import mazie.model.Artifact;
import mazie.model.ArtifactType;

public abstract class Monster {

    private final String name;
    private final String[] actions;
    private final String finalMessage;
    private final int attack;
    private final int defence;
    private int hp;
    private final int xpReward;
    private final Artifact artifact;

    private static final Random random = new Random();

    protected Monster(String name, String[] actions, String finalMessage, int heroLevel, int baseAttack, int baseDefence, int baseHp, int baseXpReward) {
        this.name = name;
        this.actions = actions;
        this.finalMessage = finalMessage;
        this.attack = calcForLevel(baseAttack, heroLevel);
        this.defence = calcForLevel(baseDefence, heroLevel);
        this.hp = calcForLevel(baseHp, heroLevel);
        this.xpReward = calcForLevel(baseXpReward, heroLevel);
        this.artifact = createArtifact();
    }

    public String getName() {
        return this.name;
    }

    public String getAction() {
        if (actions.length == 0) {
            throw new ModelException("%s has no action".formatted(this.name));
        }
        return actions[random.nextInt(actions.length)];
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

    public int getXpReward() {
        final var xpBonus = 42;
        return this.xpReward + xpBonus;
    }

    public Artifact getArtifact() {
        return this.artifact;
    }

    private Artifact createArtifact() {
        final var drop = random.nextInt(5);

        final var type = switch (drop) {
            case 0 -> ArtifactType.WEAPON;
            case 1 -> ArtifactType.ARMOUR;
            case 2 -> ArtifactType.HELMET;
            default -> null;
        };
        if (type == null) {
            return null;
        }
        return new Artifact(type.randomName(), type, calcArtifactValue());
    }

    private int calcArtifactValue() {
        final var value = this.xpReward / 20;
        return Math.max(value, 1);
    }

    public void takeDamage(int damage) {
        final var damageSum = damage - this.defence;
        final var totalDamage = Math.max(damageSum, 1);
        System.out.println("damage to monster: " + totalDamage); //#todo remove (debugging)
        this.hp -= totalDamage;
    }

    public boolean isDead() {
        return this.hp <= 0;
    }

    private int calcForLevel(int val, int level) {
        final var levelIncrement = Math.pow(1.10, level - 1) + 0.1;
        return (int) (val * levelIncrement);
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
