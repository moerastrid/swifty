package mazie.exception;

public class InvalidInputException extends RuntimeException {
	public InvalidInputException(String message) {
		super(message);
	}

	public InvalidInputException(String message, Throwable e) {
		super(message, e);
	}
}
