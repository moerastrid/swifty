package mazie.exception;

import java.util.Set;

public class InvalidInputException extends RuntimeException {
	public InvalidInputException(String message) {
		super(message);
	}

	public InvalidInputException(String message, Throwable e) {
		super(message, e);
	}
}
