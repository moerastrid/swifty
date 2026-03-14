package mazie.model.hero;

public class Hero {
    private int id = 0;
    private String name;
    private HeroType type;
    private int level = 1;
    private int xp = 0;
    private int attack = 10;
    private int defense = 10;
    private int hp = 100;

    public Hero(String name, HeroType type) {
        this.name = name;
        this.type = type;
    }

    public Hero(String name, HeroType type, int level, int xp, int attack, int defense, int hp) {
        this.name = name;
        this.type = type;
        this.level = level;
        this.xp = xp;
        this.attack = attack;
        this.defense = defense;
        this.hp = hp;
    }

    public Hero(int id, String name, HeroType type, int level, int xp, int attack, int defense, int hp) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.level = level;
        this.xp = xp;
        this.attack = attack;
        this.defense = defense;
        this.hp = hp;
    }

    public void gainxp(int exp) {
        this.xp += exp;

        int bound = this.level * 1000 + (this.level - 1) * (this.level - 1) * 450;
        if (this.xp >= bound) {
            levelUp();
            this.xp -= bound;
        }
    }

    private void levelUp() {
        this.level++;
        System.out.println("%s%s leveled up to level %d!".formatted(name, type.toString(), level));
        this.attack += 1;
        this.defense += 1;
        this.hp += 10;
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

    public int getDefense() {
        return this.defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getHp() {
        return this.hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    @Override
    public String toString() {
        return "%s %s (Level %d)".formatted(type, name, level);
    }
}
