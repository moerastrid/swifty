package ajav.controller;

import java.util.Set;

import static ajav.controller.InputConstants.QUIT_OPTIONS;

public class InputValidator {
	public boolean isQuit(String input) {
		if (input == null)
			return false;
		return QUIT_OPTIONS.contains(cleanInput(input));
	}

	public boolean isValidMenuInput(String input, Set<String> validInputs) {
		if (input == null)
			return false;
		return validInputs.contains(cleanInput(input));
	}

	public boolean isValidTextInput(String input) {
		if (input == null)
			return false;
		final var cleanInput = cleanInput(input);

		return !(cleanInput.isBlank() || cleanInput.isEmpty() || this.isQuit(cleanInput));
	}

	private String cleanInput(String input) {
		return input.trim().toUpperCase();
	}
}