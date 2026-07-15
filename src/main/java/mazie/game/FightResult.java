package mazie.game;

import mazie.model.Artifact;

public record FightResult(
        boolean win,
        boolean levelup,
        Artifact drop,
        int damageToHero
) {
}
