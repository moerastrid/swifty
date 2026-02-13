package mazie.model.hero;

import mazie.model.artifact.Armor;
import mazie.model.artifact.Helm;
import mazie.model.artifact.Weapon;

public abstract class Hero {
    protected String name;
    protected HeroType type;
    protected int level = 1;
    protected int experience = 0;
    protected int attack = 10;
    protected int defense = 10;
    protected int hp = 100;
    protected Armor armor;
    protected Helm helm;
    protected Weapon weapon;

    protected Hero(String name) {
        this.name = name;
    }

    public void gainExperience(int exp) {
        this.experience += exp;

        int bound = this.level * 1000 + (this.level - 1) * (this.level - 1) * 450;
        if (this.experience >= bound) {
            levelUp();
            this.experience -= bound;
        }
    }

    public void levelUp() {
        this.level++;
        System.out.println("%s%s leveled up to level %d!".formatted(name, type.toString(), level));
        this.attack += 1;
        this.defense += 1;
        this.hp += 10;
    }

    public int getLevel() {
        return level;
    }

    public HeroType getType() {
        return type;
    }

    public int getHp() {
        return hp;
    }

    public Armor getArmor() {
        return armor;
    }

    public Helm getHelm() {
        return helm;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
    }

    public void setHelm(Helm helm) {
        this.helm = helm;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

	public void setName(String name) {
		this.name = name;
	}

    @Override
    public String toString() {
        return "%s%s(lvl %d): - HP: %d, Attack: %d, Defense: %d, EXP: %d".formatted(
                name, type.toString(), level, hp, attack, defense, experience);
    }

}
