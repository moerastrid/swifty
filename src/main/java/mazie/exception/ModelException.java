package mazie.exception;

public class ModelException extends RuntimeException {
    private static final String ERROR_MESSAGE = "model error: %s";

    public ModelException(String message) {
        super(ERROR_MESSAGE.formatted(message));
    }
}
