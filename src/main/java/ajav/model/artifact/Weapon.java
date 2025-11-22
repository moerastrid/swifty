package ajav.model.artifact;

public class Weapon extends Artifact {

    public Weapon(String name, String description, int value) {
        super(name, description, value);
        this.type = ArtifactType.WEAPON;
    }
}
