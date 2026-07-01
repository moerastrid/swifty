package mazie.repository;

import java.util.List;

import mazie.model.Hero;

public interface HeroRepository {

    List<Hero> loadAllHeroes();

    void save(Hero hero);

    void update(Hero hero);

    void delete(Hero hero);

}
