package mazie.model.artifact;

public class Armor extends Artifact {

    public Armor(String name, String description, int value) {
        super(name, description, value);
        this.type = ArtifactType.ARMOR;
    }
}
