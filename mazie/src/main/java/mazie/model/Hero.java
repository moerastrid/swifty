package mazie.model;

import java.util.ArrayList;
import java.util.List;

public class Hero {

    private int id = 0;
    private String name = "default";
    private HeroType type = null;
    private int level = 1;
    private int xp = 0;
    private int attack = 10;
    private int defence = 10;
    private int hp = 100;
    private Artifact weapon = null;
    private Artifact armour = null;
    private Artifact helmet = null;

    public Hero() {
    }

    public Hero(String name, HeroType type) {
        this.name = name;
        this.type = type;
    }

    public void gainXp(int xp) {
        this.xp += xp;
    }

    public boolean levelUp() {
        final var xpNeed = (this.level * 1000) + ((level - 1) * (level - 1)) * 450;

        if (this.xp > xpNeed) {
            this.level += 1;
            this.xp -= xpNeed;
            return true;
        }
        return false;
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
        if (type == null)
            throw new RuntimeException("that aint my type :(");
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
        return (this.weapon == null) ? this.attack : this.attack + this.weapon.getValue();
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefence() {
        return this.defence;
    }

    public int getTotalDefence() {
        return (this.armour == null) ? this.defence : this.defence + this.armour.getValue();
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public int getHp() {
        return this.hp;
    }

    public int getTotalHp() {
        return (this.helmet == null) ? this.hp : this.hp + this.helmet.getValue();
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public Artifact getWeapon() {
        return this.weapon;
    }

    public void setWeapon(Artifact weapon) {
        if (weapon == null || weapon.getType() != ArtifactType.WEAPON)
            throw new RuntimeException("thats no weapon :(");
        this.weapon = weapon;
    }

    public Artifact getArmour() {
        return this.armour;
    }

    public void setArmour(Artifact armour) {
        if (armour == null || armour.getType() != ArtifactType.ARMOUR)
            throw new RuntimeException("thats no armour :(");
        this.armour = armour;
    }

    public Artifact getHelmet() {
        return this.helmet;
    }

    public void setHelmet(Artifact helmet) {
        if (helmet == null || helmet.getType() != ArtifactType.HELMET)
            throw new RuntimeException("thats no helmet :(");
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
        if (artifact == null)
            throw new RuntimeException("thats null :(");

        switch (artifact.getType()) {
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

        final var artifacts = this.getArtifacts();

        final var sb = new StringBuilder(String.format(
            """
            Hero(#%d) %s identifies as a %s,
             lvl:%d, xp:%d, attack:%d, defence:%d, hp:%d,
             and is wearing:
            """,
        this.id, this.name, this.type, this.level, this.xp, this.attack, this.defence, this.hp));

        if (artifacts.isEmpty()) {
            sb.append("\t- nothing.");
        } else {
            artifacts.stream().forEach(artifact -> {
                if (artifact != null) {
                    sb.append("\t-").append(artifact.toString()).append("\n");
                }
            });
        }

        return sb.toString();
    }
}
