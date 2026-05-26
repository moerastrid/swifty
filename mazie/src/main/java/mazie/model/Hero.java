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
    private List<Artifact> artifacts = new ArrayList<>();

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

    public List<Artifact> getArtifacts() {
        return this.artifacts;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public Artifact getArtifact(int index) {
        return this.artifacts.get(index);
    }

    public void addArtifact(Artifact artifact) {
        this.artifacts.add(artifact);
    }

    public void removeArtifact(Artifact artifact) {
        this.artifacts.remove(artifact);
    }

    @Override
    public String toString() {
        // return String.format("id:%d, name:%s, type:%s, level:%d, xp:%d, attack:%d, defence:%d, hp:%d, artifacts:%s", 
        // this.id, this.name, this.type, this.level, this.xp, this.attack, this.defence, this.hp, this.artifacts);

        StringBuilder sb = new StringBuilder(String.format(
                """
				Hero(#%d) %s identifies as a %s,
				lvl:%d, xp:%d, attack:%d, defence:%d, hp:%d,
				is carrying: 
					""",                
				this.id, this.name, this.type, this.level, this.xp, this.attack, this.defence, this.hp
				// this.artifacts.stream().map(artifact -> artifact.toString()).collect(Collectors.joining())
			));

		if (artifacts.isEmpty()) {
			sb.append("\t- nothing.");
		} else {
 			this.artifacts.stream().forEach(artifact -> 
				sb.append("\t-").append(artifact.toString()).append("\n"));
		}

		return sb.toString();
    }
}
