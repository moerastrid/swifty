package mazie.model;

import java.util.Random;

public record Artifact(
        String name,
        ArtifactType type,
        int value
) {

    public static Artifact createArtifact(final int value) {
        final var random = new Random();
        final var drop = random.nextInt(5);

        final var type = switch (drop) {
            case 0 -> ArtifactType.WEAPON;
            case 1 -> ArtifactType.ARMOUR;
            case 2 -> ArtifactType.HELMET;
            default -> null;
        };

        if (type == null)
            return null;

        return new Artifact(type.randomName(), type, value);
    }

    public String getAction() {
        return this.type.actionFor(this.name);
    }

    @Override
    public String toString() {
        return String.format("%s, type:%s, value:%d",
                this.name, this.type, this.value);
    }
}
