package ajav.util;

import java.util.Random;

import ajav.model.Quality;

public final class QualityDefiner {
    private static Random random = new Random();

    private QualityDefiner() {}

    public static Quality defineQuality() {
        return switch (random.nextInt(5)) {
            case 0 -> Quality.BAD;
            case 4 -> Quality.GOOD;
            default -> Quality.NORMAL;
        };
    }
}
