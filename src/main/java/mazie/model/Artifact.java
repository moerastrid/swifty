package mazie.model;


public record Artifact(
        String name,
        ArtifactType type,
        int value
) {

    public String getAction() {
        return type.actionFor(name);
    }

    @Override
    public String toString() {
        return String.format("%s, type:%s, value:%d",
                name, type, value);
    }
}
