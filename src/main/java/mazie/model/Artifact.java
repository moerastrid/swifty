package mazie.model;


public record Artifact(
        String name,
        ArtifactType type,
        int value
) {

    public String getAction() {
        return this.type.actionFor(this.name);
    }

    @Override
    public String toString() {
        return String.format("%s, type:%s, value:%d",
                this.name, this.type, this.value);
    }
}
