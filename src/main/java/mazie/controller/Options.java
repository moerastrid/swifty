package mazie.controller;

import java.util.Set;

public class Options {

    private Options() {}

    public static final Set<String> QUIT = Set.of("Q", "QUIT");
	public static final Set<String> SELECT_HERO = Set.of("P", "PENGUIN", "F", "FROG", "B", "BEAR", "H", "HARE", "T", "TURTLE");
	public static final Set<String> INTRO = Set.of("C", "CONTINUE");
	public static final Set<String> SETUP = Set.of("N", "NEW", "L", "LOAD");
	public static final Set<String> MOVE = Set.of("U", "UP", "D", "DOWN", "L", "LEFT", "R", "RIGHT");
}
