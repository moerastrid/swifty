package mazie.model.hero;

public class Frog extends Hero {
    public Frog(String name) {
        super(name);
        this.type = HeroType.FROG;
    }

    @Override
    public void gainExperience(int exp) {
        super.gainExperience((int)(exp * 1.2));
    }
}
