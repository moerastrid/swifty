package mazie.model;

import java.util.Random;

public record Artifact(
        String name,
        ArtifactType type,
        int value
        ) {

    private static final Random RANDOM = new Random();
    private static final String[] WEAPONS = {"a knife", "a fork", "a spoon"};
    private static final String[] ARMOURS = {"a glitterlegging", "a dress"};
    private static final String[] HELMETS = {"a colander", "a carton box", "a little hat", "a yankee with no brim"};

    public static Artifact weapon(final int value) {
        final String name = WEAPONS[RANDOM.nextInt(WEAPONS.length)];

        return new Artifact(name, ArtifactType.WEAPON, value);
    }

    public static Artifact armour(final int value) {
        final String name = ARMOURS[RANDOM.nextInt(ARMOURS.length)];

        return new Artifact(name, ArtifactType.ARMOUR, value);
    }

    public static Artifact helmet(final int value) {
        final String name = HELMETS[RANDOM.nextInt(HELMETS.length)];

        return new Artifact(name, ArtifactType.HELMET, value);
    }

    @Override
    public String toString() {
        return String.format("%s, type:%s, value:%d",
                this.name, this.type, this.value);
    }
}
