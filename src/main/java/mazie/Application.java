package mazie;

import java.util.concurrent.atomic.AtomicBoolean;
import mazie.controller.GameController;
import mazie.exception.FatalException;
import mazie.exception.ModelException;
import mazie.exception.ParseException;
import mazie.exception.QuitException;
import mazie.exception.RepositoryException;
import mazie.repository.SQLiteHeroRepository;
import mazie.view.gui.GuiView;
import mazie.view.terminal.TerminalView;

public class Application {
    public Application() {}

    private static final int EX_SUCCESS = 0;
    private static final int EX_GENERAL = 1;
    private static final int EX_USAGE = 64;
    private static final int EX_UNAVAILABLE = 69;
    private static final int EX_SOFTWARE = 70;

    private static volatile boolean edtCrashed = false;
    private static final AtomicBoolean shuttingDown = new AtomicBoolean(false);
    private static GameController controller;

    public int run(final String[] args) {
        threadConfig();

        try {
            startApplication(args);
        } catch (FatalException | RepositoryException ex) {
            return safeExit(ex.getMessage(), EX_UNAVAILABLE);
        } catch (ModelException | IllegalArgumentException | IllegalStateException ex) {
            return safeExit(ex.getMessage(), EX_SOFTWARE);
        } catch (ParseException ex) {
            return safeExit(ex.getMessage(), EX_USAGE);
        } catch (QuitException ex) {
            return safeExit(ex.getMessage(), EX_SUCCESS);
        } catch (Throwable t) {
            return safeExit(t.getMessage(), EX_GENERAL);
        }

        if (edtCrashed) {
            return safeExit(EX_SOFTWARE);
        }
        return safeExit(EX_SUCCESS);
    }

    private static int safeExit(String message, int exitCode) {
        System.err.println(message);
        return safeExit(exitCode);
    }

    private static int safeExit(int exitCode) {
        if (shuttingDown.compareAndSet(false, true)) {
            System.exit(exitCode);
        }
        return exitCode;
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
                if (controller != null) {
                    controller.close();
                }
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }));
    }

    private static void startApplication(final String[] args) {
        final var console = parse(args);
        final var view = console ? new TerminalView() : new GuiView();
        final var repository = new SQLiteHeroRepository();
        controller = new GameController(view, repository);

        controller.start();
        controller.close();
    }

    private static boolean parse(final String[] args) {
        if (args.length == 0) {
            return false;
        }

        if (args.length > 1) {
            throw new ParseException("hold up, not so argumentative");
        }

        return switch (args[0].strip().toLowerCase()) {
            case "console", "c" -> true;
            case "gui", "g" -> false;
            default -> throw new ParseException("unknown argument: [%s]".formatted(args[0]));
        };
    }
}
