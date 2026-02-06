package mazie.controller;

import java.util.Set;

public final class Prompts {

	private Prompts() {}

	public static final Set<String> HERO_TYPES = Set.of("P", "PENGUIN", "F", "FROG", "B", "BEAR", "H", "HARE", "T", "TURTLE");
	public static final Set<String> INTRO_OPTIONS = Set.of("C", "CONTINUE");
	public static final Set<String> SETUP_OPTIONS = Set.of("N", "NEW", "L", "LOAD");
	public static final Set<String> QUIT_OPTIONS = Set.of("Q", "Quit");
	public static final Set<String> MAP_OPTIONS = Set.of("U", "UP", "D", "DOWN", "L", "LEFT", "R", "RIGHT");

	public static final String QUIT_PROMPT = """

																		... or [Q] to quit.
	""";

	public static final String INTRO_PROMPT = """
		Welcome to Mazie!
		- An a-maze-ing RPG -
		
		To play the game, please enter commands in the console.
		Possible commands will be shown in the prompt, between brackets [HERE].
		For example, to move up, enter the command 'up', shown as [UP].
		You can always quit the game by entering 'Q' or 'q'.
		
		Let's get started and try your first command!

		[C] continue
	""" + QUIT_PROMPT;

	public static final String SETUP_PROMPT = """
		Awesome! What now?

		[N] New game
		[L] Load a saved game
	""" + QUIT_PROMPT;

	public static final String choose_hero_prompt(String options) { 
		return """
			Choose your fighter:

		""" + QUIT_PROMPT;
	}


	public static final String NAME_HERO_PROMPT = """
		Name your fighter:
	""" + QUIT_PROMPT;

	public static final String load_game_prompt(String options) {
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
}
