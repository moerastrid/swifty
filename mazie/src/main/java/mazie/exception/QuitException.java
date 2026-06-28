package mazie.exception;

public class QuitException extends RuntimeException {
    public QuitException(String message) {
        super(message);
    }

    public QuitException(String message, Exception e) {
        super(message, e);
    }
}
