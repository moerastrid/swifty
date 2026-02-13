package mazie.controller;

import java.util.Set;
import java.util.List;

import jakarta.validation.ConstraintViolation;
import mazie.model.hero.Hero;
import mazie.view.GameView;

public final class Prompts {

	private Prompts() {
	}

	public static final String QUIT = """
															... or [Q] to quit.
	""";

	public static final String INTRO = """
		Welcome to Mazie!
		- An a-maze-ing RPG -

		To play the game, enter commands in the console.
		It speaks for itself.

		Prepare yourself to enter the maze ...
	""" + INPUT_OPTIONS(Options.INTRO);

	public static final String SETUP = """
		Play a new game, or load an existing one?
	""" + INPUT_OPTIONS(Options.SETUP);

	public static final String SELECT_HERO(List<Hero> heroes) {
		return """
			Choose your fighter:

			%s
		""".formatted(String.join("\n\t", heroes.stream().map(h -> h.toString()).toList())) 
		+ INPUT_OPTIONS(Options.SELECT_HERO);
	}

	public static final String NAME_HERO = """
			Name your fighter:
		""";

	public static final String LOAD_GAME_PROMPT(String options) {
		return """
			Load a game...
			Choose a game to load:
			%s
		""".formatted(options);
	}

	public static final String QUIT_INFO = """
		Q is for Quitters
	""";

	public static final String NEW_GAME_INFO = """
		Starting a new game...
	""";

	public static final String NO_LOAD_GAME_INFO = """
		No games to load yet...
	""";

	public static final String INVALID_INPUT(String input) {
		return """
			Invalid input [%s].
		""".formatted(input);
	}

	public static final String VIOLATIONS(Set<ConstraintViolation<GameView>> violations) {
		return String.join(", ", violations.stream().map(v -> v.getMessage()).toList());
	}

	public static final String INPUT_OPTIONS(Set<String> options) {
	return """

		Available commands: [%s]
	""".formatted(String.join("], [", options)) + QUIT;
	}
}
