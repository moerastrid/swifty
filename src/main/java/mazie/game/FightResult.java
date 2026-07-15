package mazie.game;

import mazie.model.Artifact;

public record FightResult(
        boolean win,
        boolean levelUp,
        Artifact drop,
        int damageToHero
) {
}
