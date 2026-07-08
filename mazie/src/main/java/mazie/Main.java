package mazie;

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

    public static void main(final String[] args) {
        threadConfig();

        try {
            run(args);
        } catch (FatalException ex) {
            System.err.println(ex.getMessage());
            System.exit(EX_UNAVAILABLE);
        } catch (ModelException | IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(EX_SOFTWARE);
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            System.exit(EX_USAGE);
        } catch (QuitException ex) {
            System.err.println(ex.getMessage());
            System.exit(EX_SUCCESS);
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            System.exit(EX_GENERAL);
        }

        if (edtCrashed) {
            System.exit(EX_SOFTWARE);
        }
        System.exit(EX_SUCCESS);
    }

    private static void threadConfig() {
        final var mainThread = Thread.currentThread();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Thread [%s] error: %s".formatted(thread.getName(), throwable.getMessage()));
            edtCrashed = true;
            mainThread.interrupt();
        });
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
