package mazie.exception;

public class QuitException extends RuntimeException {
	public QuitException(String message) {
		super(message);
	}

	public QuitException(String message, Throwable e) {
		super(message, e);
	}
}
