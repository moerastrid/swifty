package mazie.exception;

public class UnexpectedException extends RuntimeException {
	public UnexpectedException(String message) {
		super(message);
	}

	public UnexpectedException(String message, Throwable e) {
		super(message, e);
	}
}
