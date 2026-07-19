package mazie.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mazie.exception.ModelException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hero {

    private int id = 0;

    @NotBlank(message = "Heroes need names")
    @Size(min = 2, max = 30)
    private final String name;

    @NotNull(message = "You can't just be a 'hero'")
    private final HeroType type;

    @Min(1)
    private int level = 1;

    @Min(0)
    private int xp = 0;

    private int attack;
    private int defence;
    private int hp;

    private Artifact weapon = null;
    private Artifact armour = null;
    private Artifact helmet = null;

    public Hero(String name, HeroType type) {
        this.name = name;
        this.type = type;
        this.attack = type.baseAttack;
        this.defence = type.baseDefence;
        this.hp = type.baseHp;
    }

    public Hero(int id, String name, HeroType type, int level, int xp, int attack, int defence, int hp) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.level = level;
        this.xp = xp;
        this.attack = attack;
        this.defence = defence;
        this.hp = hp;
    }

    public int takeDamage(int damage) {
        final var damageSum = damage - getTotalDefence();
        final var totalDamage = Math.max(damageSum, 1);
        hp -= totalDamage;
        return totalDamage;
    }

    public boolean isDead() {
        return getTotalHp() <= 0;
    }

    /*
        returns true if lvlUp = true.
     */
    public boolean gainXp(int xp) {
        final var xpNeed = (level * 1000) + ((level - 1) * (level - 1)) * 450;

        this.xp += xp;

        if (this.xp >= xpNeed) {
            lvlUp();
            return true;
        }
        return false;
    }

    private void lvlUp() {
        level += 1;

        final var levelIncrement = Math.pow(1.1, (level - 1));
        attack = (int) (type.baseAttack * levelIncrement);
        defence = (int) (type.baseDefence * levelIncrement);

        final var hpMax = (int) (type.baseHp * levelIncrement);
        final var newHp = hp + (hpMax / 2);
        hp = Math.min(newHp, hpMax);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public HeroType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getAttack() {
        return attack;
    }

    public int getTotalAttack() {
        return (weapon == null) ? attack : attack + weapon.value();
    }

    public String getAttackString() {
        if (weapon == null) {
            return "%d".formatted(attack);
        }
        return "%d (%d + %d)".formatted(getTotalAttack(), attack, weapon.value());
    }

    public int getDefence() {
        return defence;
    }

    private int getTotalDefence() {
        return (armour == null) ? defence : defence + armour.value();
    }

    public String getDefenceString() {
        if (armour == null) {
            return "%d".formatted(defence);
        }
        return ("%d (%d + %d)".formatted(getTotalDefence(), defence, armour.value()));
    }

    public int getHp() {
        return hp;
    }

    private int getTotalHp() {
        return (helmet == null) ? hp : hp + helmet.value();
    }

    public String getHpString() {
        if (helmet == null) {
            return "%d".formatted(hp);
        }
        return ("%d (%d + %d)".formatted(getTotalHp(), hp, helmet.value()));
    }

    public List<Artifact> getArtifacts() {
        final var artifacts = new ArrayList<Artifact>();
        if (weapon != null) {
            artifacts.add(weapon);
        }
        if (armour != null) {
            artifacts.add(armour);
        }
        if (helmet != null) {
            artifacts.add(helmet);
        }
        return artifacts;
    }

    public void setArtifact(Artifact artifact) {
        if (artifact == null) {
            throw new ModelException("that artifact is null :(");
        }

        switch (artifact.type()) {
            case WEAPON -> weapon = artifact;
            case ARMOUR -> armour = artifact;
            case HELMET -> helmet = artifact;
        }
    }

    public String getAction() {
        final var random = new Random();
        final var actions = new ArrayList<>(List.of(
                "makes a joke to lighten the mood",
                "pretends their mom just called, we need to leave right away",
                "finds a quiet corner",
                "starts rambling about their latest interest",
                "hums a soft melody",
                "shows you pictures of their pet snail",
                "spots a really cool bird",
                "zoned out (professionally)"
        ));

        final var artifacts = getArtifacts();
        if (!artifacts.isEmpty()) {
            final var artifactActions = artifacts.stream().map(Artifact::getAction).toList();
            // (adding the artifact Actions twice for having a higher chance of getting those)
            actions.addAll(artifactActions);
            actions.addAll(artifactActions);
        }

        return "%s %s".formatted(name, actions.get(random.nextInt(actions.size())));
    }

    @Override
    public String toString() {
        return "Hero(#%d) %s %s, lvl:%d, xp:%d, attack:%d, defence:%d,y hp:%d, artifacts:%s"
                .formatted(id, name, type, level, xp, attack, defence, hp, getArtifacts());
    }
}
