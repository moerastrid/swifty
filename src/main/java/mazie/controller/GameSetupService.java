package mazie.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.stream.Collectors;
import mazie.exception.DuplicateNameException;
import mazie.model.Hero;
import mazie.repository.HeroRepository;
import mazie.view.GameView;

public record GameSetupService(Validator validator, GameView view, HeroRepository repository) {

    public Hero setupHero() {
        final var heroes = repository.loadAllHeroes();

        Hero hero = null;
        while (!confirm(hero)) {
            hero = switch (defineGameType(heroes)) {
                case NEW -> createHero();
                case EXISTING -> view.selectHero(heroes);
            };
        }
        if (hero.getId() == 0) {
            try {
                repository.save(hero);
            } catch (DuplicateNameException ex) {
                view.showError(ex.getMessage());
                return setupHero();
            }
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
            return createHero();
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
