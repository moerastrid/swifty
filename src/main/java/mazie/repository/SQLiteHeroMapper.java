package mazie.repository;

import mazie.model.Artifact;
import mazie.model.ArtifactType;
import mazie.model.Hero;
import mazie.model.HeroType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SQLiteHeroMapper {

    public static Map<Integer, Hero> mapHeroes(ResultSet resultSet) throws SQLException {
        final var heroMap = new HashMap<Integer, Hero>();

        while (resultSet.next()) {
            final var heroId = resultSet.getInt("id");
            final var heroName = resultSet.getString("name");
            final var heroType = resultSet.getString("type");
            final var heroLevel = resultSet.getInt("level");
            final var heroXp = resultSet.getInt("xp");
            final var heroAttack = resultSet.getInt("attack");
            final var heroDefence = resultSet.getInt("defence");
            final var heroHp = resultSet.getInt("hp");
            final var artifactName = resultSet.getString("a_name");
            final var artifactType = resultSet.getString("a_type");
            final var artifactValue = resultSet.getInt("a_value");

            var hero = heroMap.get(heroId);
            if (hero == null) {
                hero = new Hero(heroId, heroName, HeroType.valueOf(heroType), heroLevel, heroXp, heroAttack, heroDefence, heroHp);
                heroMap.put(heroId, hero);
            }

            if (artifactName != null) {
                final var artifact = new Artifact(artifactName, ArtifactType.valueOf(artifactType), artifactValue);
                hero.setArtifact(artifact);
            }
        }
        return heroMap;
    }
}
