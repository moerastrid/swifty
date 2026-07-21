package mazie;

import java.util.concurrent.atomic.AtomicBoolean;

import mazie.bootstrap.Application;
import mazie.exception.QuitException;

public class Main {

    private static final int EX_SUCCESS = 0;
    private static final int EX_GENERAL = 1;

    private static volatile boolean edtCrashed = false;
    private static final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    private static Application application;

    private Main() {}

    public static void main(final String[] args) {
        threadConfig();

        try {
            application = new Application(args);
            application.start();
        } catch (RuntimeException ex) {
            System.err.println(ex.getMessage());
            safeExit(setExitCode(ex));
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
            if (shuttingDown.compareAndSet(false, true)) {
                mainThread.interrupt();
                shutDownApp();
                try {
                    mainThread.join(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }));
    }

    private static void shutDownApp() {
        if (application == null) {
            return;
        }
        try {
            application.shutDownGracefully();
        } catch (RuntimeException ex) {
            System.err.printf("WARNER: shutdown failed: %s%n", ex.getMessage());
            throw ex;
        }
    }

    private static int setExitCode(RuntimeException ex) {
        if (edtCrashed) {
            return EX_GENERAL;
        }
        if (ex instanceof QuitException) {
            return EX_SUCCESS;
        }
        return EX_GENERAL;
    }

    private static void safeExit(int exitCode) {
        if (shuttingDown.compareAndSet(false, true)) {
            shutDownApp();
            System.exit(exitCode);
        }
    }
}
