package ajav.exception;

public class FatalErrorException extends RuntimeException {
	public FatalErrorException(String message) {
		super(message);
	}

	public FatalErrorException(String message, Throwable e) {
		super(message, e);
	}
}