package mazie.controller;

import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;

import java.lang.reflect.Method;

import jakarta.validation.Validation;
import mazie.exception.FatalException;
import mazie.exception.InvalidInputException;
import mazie.view.GameView;

public class GameValidator {
	private GameView view;
	private final Validator validator;
	private final ExecutableValidator executableValidator;
	private Method method;

	public GameValidator(GameView view) {
		this.view = view;
		this.validator = Validation.buildDefaultValidatorFactory().getValidator();
		this.executableValidator = validator.forExecutables();
		try {
			method = view.getClass().getMethod("getInput");
		} catch (NoSuchMethodException e) {
			throw new FatalException("Error in GameValidator: no such method exception", e);
		}
	}
    
    void validate(String input) throws InvalidInputException {

		final var violations = executableValidator.validateReturnValue(view, method, input);

		if (!violations.isEmpty()) {
			final var stringBuilder = new StringBuilder();
			stringBuilder.append("Invalid input [")
				.append(input)
				.append("]: ");
			violations.forEach(v 
				-> stringBuilder.append(v.getMessage()).append(" "));
			throw new InvalidInputException(stringBuilder.toString());
		}
    }
}
