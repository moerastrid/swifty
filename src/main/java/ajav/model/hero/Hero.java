package ajav.model.hero;

public abstract class Hero {
    protected String name;
    protected HeroType type;
    protected int level = 1;
    protected int experience = 0;
    protected int attack = 10;
    protected int defense = 10;
    protected int hp = 100;

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

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    @Override
    public String toString() {
        return "%s%s(lvl %d): - HP: %d, Attack: %d, Defense: %d, EXP: %d".formatted(
                name, type.toString(), level, hp, attack, defense, experience);
    }
}
