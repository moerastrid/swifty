package mazie.model.util;

import java.util.Random;

import mazie.model.Quality;

public final class QualityDefiner {
    private static final Random random = new Random();

    private QualityDefiner() {}

    public static Quality defineQuality() {
        return switch (random.nextInt(5)) {
            case 0 -> Quality.BAD;
            case 4 -> Quality.GOOD;
            default -> Quality.NORMAL;
        };
    }
}
