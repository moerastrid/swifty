package mazie;

public class Main {

    private Main() {}

    public static void main(final String[] args) {
        final var exitCode = new Application().run(args);
        System.exit(exitCode);
    }
}
