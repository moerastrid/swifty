package mazie.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import mazie.model.Artifact;
import mazie.model.ArtifactType;
import mazie.model.Hero;
import mazie.model.HeroType;

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
                hero = new Hero(heroId, heroName, convertHeroType(heroType), heroLevel, heroXp, heroAttack, heroDefence, heroHp);
                heroMap.put(heroId, hero);
            }

            if (artifactName != null) {
                final var artifact = new Artifact(artifactName, convertArtifactType(artifactType), artifactValue);
                hero.setArtifact(artifact);
            }
        }
        return heroMap;
    }

    private static HeroType convertHeroType(String type) {
        return switch (type) {
            case "FROG" ->
                HeroType.FROG;
            case "HARE" ->
                HeroType.HARE;
            case "BEAR" ->
                HeroType.BEAR;
            default ->
                null;
        };
    }

    private static ArtifactType convertArtifactType(String type) {
        return switch (type) {
            case "WEAPON" ->
                ArtifactType.WEAPON;
            case "ARMOUR" ->
                ArtifactType.ARMOUR;
            case "HELMET" ->
                ArtifactType.HELMET;
            default ->
                null;
        };
    }
}
