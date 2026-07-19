package mazie;

import java.util.concurrent.atomic.AtomicBoolean;
import mazie.bootstrap.Application;
import mazie.exception.FatalException;
import mazie.exception.ModelException;
import mazie.exception.ParseException;
import mazie.exception.QuitException;
import mazie.exception.RepositoryException;

public class Main {

    private static final int EX_SUCCESS = 0;
    private static final int EX_GENERAL = 1;
    private static final int EX_USAGE = 64;
    private static final int EX_UNAVAILABLE = 69;
    private static final int EX_SOFTWARE = 70;

    private static volatile boolean edtCrashed = false;
    private static final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    private static Application application;

    private Main() {}

    public static void main(final String[] args) {
        threadConfig();

        try {
            application = new Application(args);
            application.start();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            safeExit(setExitCode(ex));
        }

        if (edtCrashed) {
            safeExit(EX_SOFTWARE);
        }

        safeExit(EX_SUCCESS);
    }

    private static void threadConfig() {
        final var mainThread = Thread.currentThread();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.printf("Thread [%s] error: %s%n", thread.getName(), throwable.getMessage());
            edtCrashed = true;
            mainThread.interrupt();
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            System.err.println("HALLO 1");
            if (shuttingDown.compareAndSet(false, true)) {
                System.err.println("HALLO 2");
                mainThread.interrupt();
                shutDownApp();
                try {
                    System.err.println("HALLO 3");
                    mainThread.join();
                } catch (InterruptedException e) {
                    System.err.println("HALLO 4");
                    Thread.currentThread().interrupt();
                }
            }
        }));
        System.err.println("HALLO 5");
    }

    private static void shutDownApp() {
        if (application == null) {
            return;
        }
        try {
            application.shutDownGracefully();
        } catch (Exception ex) {
            System.err.printf("WARNER: shutdown failed: %s%n", ex.getMessage());
            throw ex;
        }
    }

    private static int setExitCode(Exception ex) {
        return switch (ex) {
            case FatalException ignored -> EX_UNAVAILABLE;
            case RepositoryException ignored -> EX_UNAVAILABLE;
            case ModelException ignored -> EX_SOFTWARE;
            case IllegalArgumentException ignored -> EX_SOFTWARE;
            case IllegalStateException ignored -> EX_SOFTWARE;
            case ParseException ignored -> EX_USAGE;
            case QuitException ignored -> EX_SUCCESS;
            default -> EX_GENERAL;
        };
    }

    private static void safeExit(int exitCode) {
        if (shuttingDown.compareAndSet(false, true)) {
            System.exit(exitCode);
        }
    }
}
