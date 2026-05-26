package mazie;

import mazie.controller.GameController;
import mazie.game.GameEngine;
import mazie.view.terminal.TerminalView;

public class Main {


	    
	String[] input = {
        "UP", "UP", "DOWN"
    };

	public static void main(String[] args) {
		System.out.println("hoi :)");

		var engine = new GameEngine();
		var view = new TerminalView();

		var controller = new GameController(engine, view);

		controller.start();

	}
}
