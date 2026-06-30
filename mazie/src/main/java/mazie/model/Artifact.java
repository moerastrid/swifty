package mazie.model;

public record Artifact(
        String name,
        ArtifactType type,
        int value
        ) {

    private static final String[] WEAPONS = {"a knife", "a fork", "a spoon"};
    private static final String[] ARMOURS = {"a glitterlegging", "a dress"};
    private static final String[] HELMETS = {"a colander", "a carton box", "a little hat", "a yankee with no brim"};

    public static Artifact weapon(final int value) {
        final int time = getTimeAsInt();
        final String name = WEAPONS[getTimeAsInt() % WEAPONS.length];

        return new Artifact(name, ArtifactType.WEAPON, value);
    }

    public static Artifact armour(final int value) {
        final String name = ARMOURS[getTimeAsInt() % ARMOURS.length];

        return new Artifact(name, ArtifactType.ARMOUR, value);
    }

    public static Artifact helmet(final int value) {
        final String name = HELMETS[getTimeAsInt() % HELMETS.length];

        return new Artifact(name, ArtifactType.HELMET, value);
    }

    private static int getTimeAsInt() {
        int time = (int) System.currentTimeMillis();
        if (time < 0) {
            time = time * -1;
        }
        return time;
    }

    @Override
    public String toString() {
        return String.format("%s, type:%s, value:%d",
                this.name, this.type, this.value);
    }
}
