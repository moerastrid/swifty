package mazie.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mazie.exception.ModelException;

public class Hero {

    private int id = 0;

    @NotBlank(message = "Heroes need names")
    @Size(min = 2, max = 30)
    private String name = "default";

    @NotNull(message = "You can't just be a 'hero'")
    private HeroType type;

    @Min(1)
    private int level = 1;

    @Min(0)
    private int xp = 0;

    private int attack = 10;
    private int defence = 10;
    private int hp = 100;

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

    public void takeDamage(int damage) {
        final var damageSum = damage - this.defence;
        final var totalDamage = damageSum > 1 ? damageSum : 1;
        System.out.println("damage to hero: " + totalDamage); //#todo remove (debugging)
        this.hp -= totalDamage;
    }

    public boolean isDead() {
        return this.getTotalHp() <= 0;
    }

    /*
        returns true if lvlUp = true.
     */
    public boolean gainXp(int xp) {
        final var xpNeed = (this.level * 1000) + ((level - 1) * (level - 1)) * 450;

        this.xp += xp;

        if (this.xp >= xpNeed) {
            return lvlUp();
        }
        return false;
    }

    private boolean lvlUp() {
        this.level += 1;

        final var levelIncrement = Math.pow(1.1, (double)(this.level - 1));

        this.attack = (int)(type.baseAttack * levelIncrement);
        this.defence = (int)(type.baseDefence * levelIncrement);
        final var hpMax = (int)(type.baseHp * levelIncrement);

        final var newHp = this.hp + (hpMax / 2);
        this.hp = (newHp > hpMax) ? hpMax : newHp;

        return true;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public HeroType getType() {
        return this.type;
    }

    public int getLevel() {
        return this.level;
    }

    public int getXp() {
        return this.xp;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getTotalAttack() {
        return (this.weapon == null) ? this.attack : this.attack + this.weapon.value();
    }

    public String getAttackString() {
        final var bonus = (this.weapon == null) ? 0 : this.weapon.value();
        return ("%d (%d + %d)".formatted(this.getTotalAttack(), this.getAttack(), bonus));
    }

    public int getDefence() {
        return this.defence;
    }

    public int getTotalDefence() {
        return (this.armour == null) ? this.defence : this.defence + this.armour.value();
    }

    public String getDefenceString() {
        final var bonus = (this.armour == null) ? 0 : this.armour.value();
        return ("%d (%d + %d)".formatted(this.getTotalDefence(), this.getDefence(), bonus));
    }

    public int getHp() {
        return this.hp;
    }

    public int getTotalHp() {
        return (this.helmet == null) ? this.hp : this.hp + this.helmet.value();
    }

    public String getHpString() {
        final var bonus = (this.helmet == null) ? 0 : this.helmet.value();
        return ("%d (%d + %d)".formatted(this.getTotalHp(), this.getHp(), bonus));
    }

    public Artifact getWeapon() {
        return this.weapon;
    }

    public Artifact getArmour() {
        return this.armour;
    }

    public Artifact getHelmet() {
        return this.helmet;
    }

    public List<Artifact> getArtifacts() {
        final var artifacts = new ArrayList<Artifact>();
        if (this.weapon != null) {
            artifacts.add(this.weapon);
        }
        if (this.armour != null) {
            artifacts.add(this.armour);
        }
        if (this.helmet != null) {
            artifacts.add(this.helmet);
        }
        return artifacts;
    }

    public void setArtifact(Artifact artifact) {
        if (artifact == null) {
            throw new ModelException("that artifact is null :(");
        }

        switch (artifact.type()) {
            case WEAPON ->
                this.weapon = artifact;
            case ARMOUR ->
                this.armour = artifact;
            case HELMET ->
                this.helmet = artifact;
        }
    }

    @Override
    public String toString() {
        return "Hero(#%d) %s %s, lvl:%d, xp:%d, attack:%d, defence:%d, hp:%d, artifacts:%s"
                .formatted(this.id, this.name, this.type, this.level, this.xp, this.attack, this.defence, this.hp, this.getArtifacts());
    }
}
