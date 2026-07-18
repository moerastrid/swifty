package mazie.repository;

import mazie.model.Hero;

import java.util.Map;

public interface HeroRepository {

    Map<Integer, Hero> loadAllHeroes();

    void close();

    void save(Hero hero);

    void update(Hero hero);

    void delete(Hero hero);
}
