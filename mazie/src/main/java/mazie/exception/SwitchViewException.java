package mazie.exception;

public class SwitchViewException extends Throwable {

    private static final String MESSAGE = "switching view";

    public SwitchViewException() {
        super(MESSAGE);
    }
}
