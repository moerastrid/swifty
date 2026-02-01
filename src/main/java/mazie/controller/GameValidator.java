package mazie.controller;

import java.lang.reflect.Method;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.Validation;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import mazie.exception.FatalException;
import mazie.exception.InvalidInputException;
import mazie.view.GameView;

public final class GameValidator {
	private static final ExecutableValidator validator = resolveExecutableValidator();
	private static final Method method = resolveMethod();

	static void validate(GameView view, String input) throws InvalidInputException {
		final var violations = validator.validateReturnValue(view, method, input);

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
