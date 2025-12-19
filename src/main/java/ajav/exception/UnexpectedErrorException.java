package ajav.exception;

public class UnexpectedErrorException extends RuntimeException {
	public UnexpectedErrorException(String message) {
		super(message);
	}

	public UnexpectedErrorException(String message, Throwable e) {
		super(message, e);
	}
}