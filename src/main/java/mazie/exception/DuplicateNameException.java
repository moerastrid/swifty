package mazie.exception;

public class DuplicateNameException extends RuntimeException {

    public DuplicateNameException(String name) {
        super("there can only be one: [%s]".formatted(name));
    }
}
