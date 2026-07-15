package mazie.model;

import java.util.Map;
import java.util.Random;

public enum ArtifactType {
    WEAPON(Map.of(
            "a fidget spinner", "spins the fidget spinner",
            "an emergency snack", "takes a bite of their favourite snack",
            "a stress ball", "starts pulcking the stressball",
            "your emotional support cucumber", "just loves cucumeber",
            "a pebble", "licks the pebble",
            "a spoon", "tries to stick the spoon to their nose",
            "a cool stick", "starts drawing shapes in the air with their stick",
            "a kazoo", "plays the kazoo"
    )),
    ARMOUR(Map.of(
            "glitterleggings", "does a little shiny dance in the glitterleggings to shake things up",
            "a dress (it has pockets!)", "puts their hands in the pockets of the dress (did you know it has pockets?!)",
            "an oversized hoodie", "disappears in the hoodie",
            "a band shirt", "starts stating fun facts about the band on their shirt",
            "fluffy socks", "wiggles their toes"
    )),
    HELMET(Map.of(
            "noise cancelling headphones", "puts on noise cancelling headphones",
            "a cardboard box", "puts the box over their head to create a little happy space",
            "a little hat", "dances the rumba",
            "a yankee with no brim", "randomly shouts \"YANKEE WITH NO BRIM! WITH NO BRIMMM!\"",
            "sunglasses", "looks mysterious"
    ));

    private final Map<String, String> nameActionMap;

    ArtifactType(Map<String, String> nameActionMap) {
        this.nameActionMap = nameActionMap;
    }

    private static final Random RANDOM = new Random();

    public String randomName() {
        final var names = this.nameActionMap.keySet().stream().toList();
        return names.get(RANDOM.nextInt(names.size()));
    }

    public String actionFor(String name) {
        return nameActionMap.get(name);
    }
}
