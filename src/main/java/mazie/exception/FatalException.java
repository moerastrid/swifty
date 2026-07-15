package mazie.exception;

public class FatalException extends RuntimeException {
    private static final String ERROR_MESSAGE = "FATAL ERROR: %s";

    public FatalException(String message) {
        super(ERROR_MESSAGE.formatted(message));
    }

    public FatalException(String message, Throwable e) {
        super(ERROR_MESSAGE.formatted(message), e);
    }
}
