package mazie.model;

import java.util.Random;

public enum ArtifactType {
    WEAPON(new String[]{
        "a fidget spinner",
        "an emergency snack",
        "a stress ball",
        "your emotional support cucumber",
        "a pebble",
        "a spoon",
        "a cool stick",
        "a kazoo"
    }),
    ARMOUR(new String[]{
        "glitterleggings",
        "a dress (it has pockets!)",
        "an oversized hoodie",
        "a band shirt",
        "fluffy socks"
    }),
    HELMET(new String[]{
        "noise cancelling headphones",
        "a cardboard box",
        "a little hat",
        "a yankee with no brim",
        "sunglasses"
    });

    private final String[] names;

    ArtifactType(String[] names) {
        this.names = names;
    }

    private static final Random RANDOM = new Random();

    public String randomName() {
        return this.names[RANDOM.nextInt(names.length)];
    }
}
