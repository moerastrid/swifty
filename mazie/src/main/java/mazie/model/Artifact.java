package mazie.model;

import java.util.Random;

public record Artifact(
        String name,
        ArtifactType type,
        int value
        ) {

    private static final Random RANDOM = new Random();
    private static final String[] WEAPONS = {
        "a fidget spinner",
        "an emergency snack",
        "a stress ball",
        "your emotional support cucumber",
        "a pebble",
        "a spoon",
        "a cool stick", 
        "a kazoo"
    };
    private static final String[] ARMOURS = {
        "glitterleggings", 
        "a dress (it has pockets!)",
        "an oversized hoodie",
        "a band shirt",
        "fluffy socks"
    };
    private static final String[] HELMETS = {
        "noise cancelling headphones", 
        "a cardboard box", 
        "a little hat", 
        "a yankee with no brim", 
        "sunglasses"
    };

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
