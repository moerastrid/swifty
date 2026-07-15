package mazie.exception;

public class SwitchViewException extends RuntimeException {

    private static final String MESSAGE = "switching view";

    public SwitchViewException() {
        super(MESSAGE);
    }
}
