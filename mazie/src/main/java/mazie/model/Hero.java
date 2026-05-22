package mazie.model;

public class Hero {
	private int id = 0;
	private String name;
	private HeroType type;
	private int level = 1;
	private int xp = 0;
	private int attack = 10;
	private int defense = 10;
	private int hp = 100;
	private List<Artifact> artifacts;

	private Hero() {}

	public Hero(String name, HeroType type) {
		this.name = name;
		this.type = type;
	}

	public int getId() {
		return this.id;
	}

	public int setId(int id) {
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


}
