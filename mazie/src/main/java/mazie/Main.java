package mazie;

import mazie.controller.GameController;
import mazie.view.terminal.TerminalView;

public class Main {


	    
	String[] input = {
        "UP", "UP", "DOWN"
    };

	public static void main(String[] args) {
		System.out.println("hoi :)");

		var view = new TerminalView();

		var controller = new GameController(view);

		controller.start();


	}


}
