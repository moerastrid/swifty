package ajav.model.artifact;

public class Helm extends Artifact {

    public Helm(String name, String description, int value) {
        super(name, description, value);
        this.type = ArtifactType.HELM;
    }
}
