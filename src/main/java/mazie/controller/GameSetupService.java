package mazie.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.stream.Collectors;
import mazie.model.Hero;
import mazie.repository.HeroRepository;
import mazie.view.GameView;

public class GameSetupService {

    private final Validator validator;
    private final GameView view;
    private final HeroRepository repository;

    public GameSetupService(Validator validator, GameView view, HeroRepository repository) {
        this.validator = validator;
        this.view = view;
        this.repository = repository;
    }

    public Hero setupHero() {
        final var heroes = repository.loadAllHeroes();

        Hero hero = null;
        while(!this.confirm(hero)) {
            hero = switch (defineGameType(heroes)) {
                case NEW -> this.createHero();
                case EXISTING -> view.selectHero(heroes);
            };
        }
        if (hero.getId() == 0) {
            repository.save(hero);
        }
        return hero;
    }

    private GameType defineGameType(Map<Integer, Hero> heroes) {
        if (heroes.isEmpty()) {
            return GameType.NEW;
        }
        if (view.askNewGame()) {
            return GameType.NEW;
        }
        return GameType.EXISTING;
    }

    private Hero createHero() {
        Hero hero = view.createHero();

        final var violations = validator.validate(hero);
        if (!violations.isEmpty()) {
            final var errorMessage = "Hero not valid: %s".formatted(
                    violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "))
            );
            view.showError(errorMessage);
            return this.createHero();
        }

        return hero;
    }

    private boolean confirm(Hero hero) {
        if (hero == null) {
            return false;
        }
        return view.confirmHero(hero);
    }

    private enum GameType {
        NEW,
        EXISTING
    }
}
