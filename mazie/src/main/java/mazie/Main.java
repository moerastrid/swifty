package mazie;

import java.util.concurrent.atomic.AtomicBoolean;

import mazie.controller.GameController;
import mazie.exception.FatalException;
import mazie.exception.ModelException;
import mazie.exception.ParseException;
import mazie.exception.QuitException;
import mazie.repository.SQLiteHeroRepository;
import mazie.view.gui.GuiView;
import mazie.view.terminal.TerminalView;

public class Main {

    private static final int EX_SUCCESS = 0;
    private static final int EX_GENERAL = 1;
    private static final int EX_USAGE = 64;
    private static final int EX_UNAVAILABLE = 69;
    private static final int EX_SOFTWARE = 70;

    private static volatile boolean edtCrashed = false;
    private static final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    public static void main(final String[] args) {
        threadConfig();

        try {
            run(args);
        } catch (FatalException ex) {
            safeExit(ex.getMessage(), EX_UNAVAILABLE);
        } catch (ModelException | IllegalArgumentException | IllegalStateException ex) {
            safeExit(ex.getMessage(), EX_SOFTWARE);
        } catch (ParseException ex) {
            safeExit(ex.getMessage(), EX_USAGE);
        } catch (QuitException ex) {
            safeExit(ex.getMessage(), EX_SUCCESS);
        } catch (Throwable t) {
            safeExit(t.getMessage(), EX_GENERAL);
        }

        if (edtCrashed) {
            safeExit(EX_SOFTWARE);
        }
        safeExit(EX_SUCCESS);
    }

    private static void safeExit(String message, int exitCode) {
        System.err.println(message);
        safeExit(exitCode);
    }

    private static void safeExit(int exitCode) {
        if (shuttingDown.compareAndSet(false, true)) {
            System.exit(exitCode);
        }
    }

    private static void threadConfig() {
        final var mainThread = Thread.currentThread();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Thread [%s] error: %s".formatted(thread.getName(), throwable.getMessage()));
            edtCrashed = true;
            mainThread.interrupt();
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (shuttingDown.compareAndSet(false, true)) {
                mainThread.interrupt();
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }));
    }

    private static void run(final String[] args) {
        final var console = parse(args);
        final var view = console ? new TerminalView() : new GuiView();
        final var repository = new SQLiteHeroRepository();
        final var controller = new GameController(view, repository);

        controller.start();
    }

    private static boolean parse(final String[] args) {
        if (args.length == 0) {
            return false;
        }

        if (args.length > 1) {
            throw new ParseException("hold up, not so argumentative");
        }

        return switch (args[0].strip().toLowerCase()) {
            case "console", "c" ->
                true;
            case "gui", "g" ->
                false;
            default ->
                throw new ParseException("unknown argument: [%s]".formatted(args[0]));
        };
    }
}
