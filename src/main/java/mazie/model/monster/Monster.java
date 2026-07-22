package mazie.model.monster;

import java.util.Random;
import mazie.exception.ModelException;
import mazie.model.Artifact;
import mazie.model.ArtifactType;

public abstract class Monster {

    private static final Random RANDOM = new Random();
    private final String name;
    private final String[] actions;
    private final String finalMessage;
    private final int attack;
    private final int defence;
    private final int xpReward;
    private final Artifact artifact;
    private int hp;

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
        return actions[RANDOM.nextInt(actions.length)];
    }

    public String getFinalMessage() {
        return finalMessage;
    }

    public int getAttack() {
        return attack;
    }

    public int counterAttack() {
        final var miss = RANDOM.nextInt(3);
        if (isDead() || miss == 1) {
            return 0;
        }
        return attack;
    }

    public int getDefence() {
        return defence;
    }

    public int getHp() {
        return hp;
    }

    public int getXpReward() {
        final var xpBonus = 42;
        return xpReward + xpBonus;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    private Artifact createArtifact() {
        final var drop = RANDOM.nextInt(5);

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
        final var value = xpReward / 20;
        final var luckFactor = switch (RANDOM.nextInt(10)) {
            case 0, 1 -> 1;
            case 2, 3 -> -1;
            case 4 -> 2;
            default -> 0;
        };

        return Math.max(value + luckFactor, 1);
    }

    public void takeDamage(int damage) {
        final var damageSum = damage - defence;
        final var totalDamage = Math.max(damageSum, 1);
        hp -= totalDamage;
    }

    public boolean isDead() {
        return hp <= 0;
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
                        """, name, attack, defence, hp
        );
    }
}
