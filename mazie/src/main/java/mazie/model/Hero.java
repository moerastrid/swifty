package mazie.model;

import java.util.ArrayList;
import java.util.List;

public class Hero {

    private int id = 0;
    private String name;
    private HeroType type;
    private int level = 1;
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

    public Artifact getWeapon() {
        return this.weapon;
    }

    public void setWeapon(Artifact weapon) {
        this.weapon = weapon;
    }

    public Artifact getArmour() {
        return this.armour;
    }

    public void setArmour(Artifact armour) {
        this.armour = armour;
    }

    public Artifact getHelmet() {
        return this.helmet;
    }

    public void setHelmet(Artifact helmet) {
        this.helmet = helmet;
    }

    public List<Artifact> getArtifacts() {
        final var artifacts = new ArrayList<Artifact>();
        artifacts.add(this.weapon);
        artifacts.add(this.armour);
        artifacts.add(this.helmet);
        return artifacts;
    }

    public void setArtifact(Artifact artifact) {
        switch (artifact.getType()) {
            case WEAPON: 
                this.setWeapon(artifact);
            case ARMOUR:
                this.setArmour(artifact);
            case HELMET:
                this.setHelmet(artifact);
        }
    }





    @Override
    public String toString() {
        // return String.format("id:%d, name:%s, type:%s, level:%d, xp:%d, attack:%d, defence:%d, hp:%d, artifacts:%s", 
        // this.id, this.name, this.type, this.level, this.xp, this.attack, this.defence, this.hp, this.artifacts);

        final var artifacts = this.getArtifacts();

        StringBuilder sb = new StringBuilder(String.format(
                """
				Hero(#%d) %s identifies as a %s,
				lvl:%d, xp:%d, attack:%d, defence:%d, hp:%d,
				is wearing: 
					""",                
				this.id, this.name, this.type, this.level, this.xp, this.attack, this.defence, this.hp
				// this.artifacts.stream().map(artifact -> artifact.toString()).collect(Collectors.joining())
			));

		if (artifacts.isEmpty()) {
			sb.append("\t- nothing.");
		} else {
 			artifacts.stream().forEach(artifact -> {
                if (artifact != null)
    				sb.append("\t-").append(artifact.toString()).append("\n");
            });
		}

		return sb.toString();
    }
}
