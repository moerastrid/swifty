package mazie.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    private Hero() {
    }

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

    /*
        returns true if lvlUp = true.
     */
    public boolean gainXp(int xp) {
        final var xpNeed = (this.level * 1000) + ((level - 1) * (level - 1)) * 450;

        this.xp += xp;

        if (this.xp > xpNeed) {
            this.xp -= xpNeed;
            return lvlUp();
        }
        return false;
    }

    private boolean lvlUp() {
        this.level += 1;
        final int hpMax = type.baseHp + ((this.level - 1) * type.baseHp / 10);
        final int newHp = this.hp + (hpMax / 2);
        this.hp = (newHp > hpMax) ? hpMax : newHp;
        this.attack = type.baseAttack + ((this.level - 1) * type.baseAttack / 10);
        this.defence = type.baseDefence + ((this.level - 1) * type.baseDefence / 10);

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

    public void setName(String name) {
        this.name = name;
    }

    public HeroType getType() {
        return this.type;
    }

    public void setType(HeroType type) {
        if (type == null) {
            throw new RuntimeException("that aint my type :(");
        }
        this.type = type;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return this.xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
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

    public void setAttack(int attack) {
        this.attack = attack;
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

    public void setDefence(int defence) {
        this.defence = defence;
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

    public void setHp(int hp) {
        this.hp = hp;
    }

    public Artifact getWeapon() {
        return this.weapon;
    }

    public void setWeapon(Artifact weapon) {
        if (weapon == null || weapon.type() != ArtifactType.WEAPON) {
            throw new RuntimeException("thats no weapon :(");
        }
        this.weapon = weapon;
    }

    public Artifact getArmour() {
        return this.armour;
    }

    public void setArmour(Artifact armour) {
        if (armour == null || armour.type() != ArtifactType.ARMOUR) {
            throw new RuntimeException("thats no armour :(");
        }
        this.armour = armour;
    }

    public Artifact getHelmet() {
        return this.helmet;
    }

    public void setHelmet(Artifact helmet) {
        if (helmet == null || helmet.type() != ArtifactType.HELMET) {
            throw new RuntimeException("thats no helmet :(");
        }
        this.helmet = helmet;
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
            throw new RuntimeException("thats null :(");
        }

        switch (artifact.type()) {
            case WEAPON ->
                this.setWeapon(artifact);
            case ARMOUR ->
                this.setArmour(artifact);
            case HELMET ->
                this.setHelmet(artifact);
        }
    }

    @Override
    public String toString() {
        return "Hero(#%d) %s %s, lvl:%d, xp:%d, attack:%d, defence:%d, hp:%d, artifacts:%s"
                .formatted(this.id, this.name, this.type, this.level, this.xp, this.attack, this.defence, this.hp, this.getArtifacts());
    }
}
