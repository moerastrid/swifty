package mazie.repository;

import java.util.Map;

import mazie.model.Hero;

public interface HeroRepository {

    Map<Integer, Hero> loadAllHeroes();

    void save(Hero hero);
    
    void update(Hero hero);

    void delete(Hero hero);
}
