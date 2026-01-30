package ajav;

import ajav.controller.GameController;
import ajav.model.GameMap;
import ajav.view.swing.SwingGui;
import ajav.view.terminal.TerminalGui;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome!");

        // tryMaps();

        // final var gui = new SwingGui();
        // gui.show();

		startTerminalGame();
		
			// startSwingGame();
    }

	private static void startTerminalGame() {

		final var gui = new TerminalGui();
		final var gameController = new GameController(gui);

		gameController.startGame();
	}

	// private static void startSwingGame() {
		
	// 	final var gui = new SwingGui();
	// 	final var gameController = new GameController(gui);

	// 	gameController.startGame();
	// }

    private static void tryMaps() {
        final var bear = HeroFactory.getInstance().newHero("BEAR", "Rico");
        final var hare = HeroFactory.getInstance().newHero("HARE", "Private");
        final var penguin = HeroFactory.getInstance().newHero("PENGUIN", "Skipper");
        final var turtle = HeroFactory.getInstance().newHero("TURTLE", "Kowalski");
        final var frog = HeroFactory.getInstance().newHero("FROG", "Greg");
        System.out.println("-------- start --------");

        final var bearGame = new GameMap(bear);
        final var hareGame = new GameMap(hare);

        for (int i = 1; i <= 12; i++) {

            System.out.println("xp += " + 500 * i);
            bear.gainExperience(500 * i);
            hare.gainExperience(500 * i);
            penguin.gainExperience(500 * i);
            turtle.gainExperience(500 * i);
            frog.gainExperience(500 * i);
            bearGame.moveLeft();
            hareGame.moveUp();
        }

        final var penguinGame = new GameMap(penguin);
        final var turtleGame = new GameMap(turtle);
        final var frogGame = new GameMap(frog);

        System.out.println("-------- end --------");
        System.out.println(bear);
        System.out.println(bearGame);
        System.out.println(hare);
        System.out.println(hareGame);
        System.out.println(penguin);
        System.out.println(penguinGame);
        System.out.println(turtle);
        System.out.println(turtleGame);
        System.out.println(frog);
        System.out.println(frogGame);
    }
}