package mazie.controller;

import java.lang.reflect.Method;
import java.util.Set;

import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.Validation;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import mazie.exception.FatalException;
import mazie.exception.InvalidInputException;
import mazie.exception.QuitException;
import mazie.view.GameView;

public final class GameValidator {
	private static final ExecutableValidator validator = resolveExecutableValidator();
	private static final Method method = resolveMethod();

	public static String validate(GameView view, String input) throws InvalidInputException {
		final var cleanInput = input.trim().toUpperCase();
		final var violations = validator.validateReturnValue(view, method, cleanInput);

		if (!violations.isEmpty()) {
			throw new InvalidInputException(Prompts.INVALID_INPUT(cleanInput) + Prompts.VIOLATIONS(violations));
		}

		return cleanInput;
    }

	public static String validate(GameView view, String input, Set<String> validOptions) throws InvalidInputException, QuitException {
		GameValidator.validate(view, input);

		final var cleanInput = input.trim().toUpperCase();
		
		if (Options.QUIT.contains(cleanInput))
			throw new QuitException(Prompts.QUIT_INFO);

		if (!validOptions.contains(cleanInput))
			throw new InvalidInputException(Prompts.INVALID_INPUT(cleanInput));

		return cleanInput;
	}

	private GameValidator() {}

	private static ExecutableValidator resolveExecutableValidator() {
		final var validatorFactory = Validation.byDefaultProvider()
			.configure()
			.messageInterpolator(new ParameterMessageInterpolator())
			.buildValidatorFactory();
	
		return validatorFactory.getValidator().forExecutables();
	}

	private static Method resolveMethod() {
		try {
			return GameView.class.getMethod("getInput");
		} catch (NoSuchMethodException e) {
			throw new FatalException("Error in GameValidator: no such method exception", e);
		}
	}
}
