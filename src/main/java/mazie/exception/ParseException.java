package mazie.exception;

public class ParseException extends RuntimeException {
    private static final String ERROR_MESSAGE = "parse error: %s";

    public ParseException(String message) {
        super(ERROR_MESSAGE.formatted(message));
    }

    public ParseException(String message, Exception e) {
        super(ERROR_MESSAGE.formatted(message), e);
    }
}
