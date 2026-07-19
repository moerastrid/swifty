package mazie.exception;

public class RepositoryException extends RuntimeException {
    private static final String ERROR_MESSAGE = "repository error: %s";

    public RepositoryException(String message) {
        super(ERROR_MESSAGE.formatted(message));
    }

    public RepositoryException(String message, Exception e) {
        super(ERROR_MESSAGE.formatted(message), e);
    }
}
