package mazie.view.terminal;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import mazie.exception.QuitException;
import mazie.exception.SwitchViewException;
import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.HeroType;
import mazie.model.monster.Monster;
import mazie.view.AbstractGameView;

public class TerminalView extends AbstractGameView {

    private static final Map<String, HeroType> HERO_TYPES = Map.of(
            "w", HeroType.WEEVIL,
            "weevil", HeroType.WEEVIL,
            "f", HeroType.FROG,
            "frog", HeroType.FROG,
            "m", HeroType.MOUSE,
            "mouse", HeroType.MOUSE
    );
    private static final Map<String, Boolean> YES_NO = Map.of(
            "y", true,
            "yes", true,
            "n", false,
            "no", false
    );
    private static final Map<String, Direction> DIRECTIONS = Map.of(
            "n", Direction.NORTH,
            "north", Direction.NORTH,
            "e", Direction.EAST,
            "east", Direction.EAST,
            "s", Direction.SOUTH,
            "south", Direction.SOUTH,
            "w", Direction.WEST,
            "west", Direction.WEST
    );
    private static final Set<String> QUIT = Set.of("q", "quit", "exit");
    private static final String SWITCH = "switch";

    private Runnable switchListener;
    private static final BlockingQueue<String> inputQueue = new SynchronousQueue<>();
    private static final AtomicBoolean scannerStarted = new AtomicBoolean(false);

    public TerminalView(Thread mainThread) {
        super(mainThread);
        startScannerOnce(mainThread);
    }

    private static void startScannerOnce(Thread mainThread) {
        if (!scannerStarted.compareAndSet(false, true)) {
            return;
        }

        final var thread = new Thread(() -> scanLoop(mainThread), "scannerThread");
        thread.setDaemon(true);
        thread.start();
    }

    private static void scanLoop(Thread mainThread) {
        /*  
            @SuppressWarnings("resource")
            explicit choise: for view switching.
            no biggie, runs on deamon thread.
        */
        @SuppressWarnings("resource")
        final var scanner = new Scanner(System.in);
        try {
            while (scanner.hasNextLine()) {
                inputQueue.put(scanner.nextLine());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        mainThread.interrupt();
    }

    private String scanNextLine() throws QuitException, SwitchViewException {
        try {
            final var line = inputQueue.take();
            final var answer = line.strip().toLowerCase();
            if (QUIT.contains(answer)) {
                throw new QuitException("user is a quitter\ndeveloper is disappointed");
            } else if (SWITCH.equals(answer)) {
                switchView();
            }
            return answer;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new QuitException("thread interruption");
        }
    }

    private void switchView() {
        switchListener.run();
        throw new SwitchViewException();
    }

    @Override
    public void close() {
        //
    }

    @Override
    public void setSwitchListener(Runnable switchListener) {
        this.switchListener = switchListener;
    }

    @Override
    public void showError(String error) {
        colourPrint(AnsiColour.YELLOW, error);
    }

    @Override
    public void showWelcome() {
        try (var inputstream = getClass().getResourceAsStream("/mazie-icon-ascii.txt")) {
            if (inputstream != null) {
                String content = new String(inputstream.readAllBytes());
                System.out.print(content);
            }
        } catch (IOException ex) {
            showError(ex.getMessage());
        }
    }

    @Override
    public boolean askNewGame() {
        final var prompt = "Do you want to start a new game? (y/n)";
        return choose(prompt, YES_NO);
    }

    @Override
    public Hero createHero() {
        final var typePrompt = """
                What do you want to be?
                %s
                """.formatted(Arrays.stream(HeroType.values()).map(type
                -> "\t- %s (%s)".formatted(type.name().toLowerCase(), type.name().toLowerCase().substring(0, 1))
        ).collect(Collectors.joining("\n")));

        HeroType type = choose(typePrompt, HERO_TYPES);

        final var namePrompt = "Name your %s: ".formatted(type);
        colourPrint(AnsiColour.PURPLE, namePrompt);

        return new Hero(scanNextLine(), type);
    }

    @Override
    public Hero selectHero(Map<Integer, Hero> heroes) {
        if (heroes == null || heroes.isEmpty()) {
            return null;
        }
        final var prompt = "choose your fighter!\n(id)";

        final var options = new HashMap<String, Hero>();

        heroes.forEach((i, hero) -> {
            showHeroStats(hero);
            options.put(i.toString(), hero);
        });

        return choose(prompt, options);
    }

    @Override
    public boolean confirmHero(Hero hero) {
        final var prompt = "Do you want to start your journey with %s? (y/n)".formatted(hero.getName());
        return choose(prompt, YES_NO);
    }

    private void showHeroStats(Hero hero) {

        final var stats = """
                Hero(#%d) %s identifies as a %s,
                  lvl     :%d
                  xp      :%d
                  attack  :%s
                  defence :%s
                  hp      :%s
                and is wearing:
                - %s
                """.formatted(hero.getId(), hero.getName(), hero.getType().name().toLowerCase(),
                hero.getLevel(),
                hero.getXp(),
                hero.getAttackString(),
                hero.getDefenceString(),
                hero.getHpString(),
                format(hero.getArtifacts()));

        colourPrint(AnsiColour.PURPLE, stats);
    }

    @Override
    public void showStartGame() {
        final var prompt = """
                
                      ╔═╝╔═ ═╔╝╔═╝╔═║  ═╔╝║ ║╔═╝  ╔╔ ╔═║══║╝╔═╝
                      ╔═╝║ ║ ║ ╔═╝╔╔╝   ║ ╔═║╔═╝  ║║║╔═║╔╝ ║╔═╝
                      ══╝╝ ╝ ╝ ══╝╝ ╝   ╝ ╝ ╝══╝  ╝╝╝╝ ╝══╝╝══╝
                """;
        colourPrint(AnsiColour.BLUE, prompt);
    }

    @Override
    public Direction askDirection(Hero hero) {
        showHeroStats(hero);
        final var prompt = "where to go? (north/east/south/west)";

        return choose(prompt, DIRECTIONS);
    }

    @Override
    public void showEmptyStep() {
        colourPrint(AnsiColour.PURPLE, "one step forward!");
    }

    @Override
    public boolean wantToFightMonster(Hero hero, Monster monster) {
        showHeroStats(hero);
        final var prompt = """
                you look around and noticed your surroundings changed..
                you've entered %s
                do you want to stay? (y/n)
                """.formatted(monster.toString());
        return choose(prompt, YES_NO);
    }

    @Override
    public void showRunSuccess(Monster monster, boolean success) {
        String prompt;

        if (success) {
            prompt = "You've escaped! %s got nothing on you!".formatted(monster.getName());
        } else {
            prompt = """
                    	You can't find the exit?!
                    	Your heart starts beating faster. There's no escape now..."
                    """;
        }
        colourPrint(AnsiColour.PURPLE, prompt);
    }

    @Override
    public void showEndGame(Hero hero, boolean win) {
        String prompt;
        showHeroStats(hero);

        if (win) {
            prompt = """
                    
                              ▄         ▄  ▄▄▄▄▄▄▄▄▄▄▄  ▄▄        ▄
                             ▐░▌       ▐░▌▐░░░░░░░░░░░▌▐░░▌      ▐░▌
                             ▐░▌       ▐░▌ ▀▀▀▀█░█▀▀▀▀ ▐░▌░▌     ▐░▌
                             ▐░▌       ▐░▌     ▐░▌     ▐░▌▐░▌    ▐░▌
                             ▐░▌   ▄   ▐░▌     ▐░▌     ▐░▌ ▐░▌   ▐░▌
                             ▐░▌  ▐░▌  ▐░▌     ▐░▌     ▐░▌  ▐░▌  ▐░▌
                             ▐░▌ ▐░▌░▌ ▐░▌     ▐░▌     ▐░▌   ▐░▌ ▐░▌
                             ▐░▌▐░▌ ▐░▌▐░▌     ▐░▌     ▐░▌    ▐░▌▐░▌
                             ▐░▌░▌   ▐░▐░▌ ▄▄▄▄█░█▄▄▄▄ ▐░▌     ▐░▐░▌
                             ▐░░▌     ▐░░▌▐░░░░░░░░░░░▌▐░▌      ▐░░▌
                              ▀▀       ▀▀  ▀▀▀▀▀▀▀▀▀▀▀  ▀        ▀▀
                    """;
        } else {
            prompt = """
                    
                  ▄▀▀▀▀▄    ▄▀▀█▄   ▄▀▀▄ ▄▀▄  ▄▀▀█▄▄▄▄
                 █         ▐ ▄▀ ▀▄ █  █ ▀  █ ▐  ▄▀   ▐
                 █    ▀▄▄    █▄▄▄█ ▐  █    █   █▄▄▄▄▄
                 █     █ █  ▄▀   █   █    █    █    ▌
                 ▐▀▄▄▄▄▀ ▐ █   ▄▀  ▄▀   ▄▀    ▄▀▄▄▄▄
                 ▐         ▐   ▐   █    █     █    ▐
                                   ▐    ▐     ▐
                  ▄▀▀▀▀▄   ▄▀▀▄ ▄▀▀▄  ▄▀▀█▄▄▄▄  ▄▀▀▄▀▀▀▄
                 █      █ █   █    █ ▐  ▄▀   ▐ █   █   █
                 █      █ ▐  █    █    █▄▄▄▄▄  ▐  █▀▀█▀
                 ▀▄    ▄▀    █   ▄▀    █    ▌   ▄▀    █
                   ▀▀▀▀       ▀▄▀     ▄▀▄▄▄▄   █     █
                                     █    ▐   ▐     ▐
                                     ▐
                    """;
        }
        colourPrint(AnsiColour.RED, prompt);
    }

    @Override
    public void showFightSummary(int damageToHero, Hero hero, Monster monster) {
        final var startSummary = "--- FIGHT SUMMARY ---";
        final var endSummary = "---------------------";
        final var damageSummary = "- %d hp     + %d xp".formatted(damageToHero, monster.getXpReward());

        colourPrint(AnsiColour.BLUE, startSummary);
        colourPrint(AnsiColour.GREEN, EmojiMap.getEmoji(hero.getType()) + hero.getAction());
        colourPrint(AnsiColour.PURPLE, EmojiMap.getEmoji(monster) + monster.getAction());
        colourPrint(AnsiColour.BLUE, damageSummary);
        colourPrint(AnsiColour.GREEN, monster.getFinalMessage());
        colourPrint(AnsiColour.BLUE, endSummary);
    }

    @Override
    public void showLevelUp(Hero hero) {
        final var prompt = """
                
                       ██▓    ▓█████ ██▒   █▓▓█████  ██▓        █    ██  ██▓███
                      ▓██▒    ▓█   ▀▓██░   █▒▓█   ▀ ▓██▒        ██  ▓██▒▓██░  ██▒
                      ▒██░    ▒███   ▓██  █▒░▒███   ▒██░       ▓██  ▒██░▓██░ ██▓▒
                      ▒██░    ▒▓█  ▄  ▒██ █░░▒▓█  ▄ ▒██░       ▓▓█  ░██░▒██▄█▓▒ ▒
                      ░██████▒░▒████▒  ▒▀█░  ░▒████▒░██████▒   ▒▒█████▓ ▒██▒ ░  ░
                      ░ ▒░▓  ░░░ ▒░ ░  ░ ▐░  ░░ ▒░ ░░ ▒░▓  ░   ░▒▓▒ ▒ ▒ ▒▓▒░ ░  ░
                      ░ ░ ▒  ░ ░ ░  ░  ░ ░░   ░ ░  ░░ ░ ▒  ░   ░░▒░ ░ ░ ░▒ ░
                        ░ ░      ░       ░░     ░     ░ ░       ░░░ ░ ░ ░░
                          ░  ░   ░  ░     ░     ░  ░    ░  ░      ░
                                         ░
                """;

        colourPrint(AnsiColour.GREEN, prompt);
        showHeroStats(hero);
    }

    @Override
    public boolean askKeepArtifact(Artifact artifact, Hero hero) {

        final var prompt = """
                	litty, an artifact!
                	%s
                	Do you want to keep it? (y/n)
                	You're currently wearing:
                	%s
                """.formatted(artifact.toString(), format(hero.getArtifacts()));

        return choose(prompt, YES_NO);
    }

    private String format(List<Artifact> artifacts) {
        if (artifacts.isEmpty()) {
            return "- nothing";
        }
        return artifacts.stream().map(artifact
                -> "- %s".formatted(artifact.toString())).collect(Collectors.joining("\n"));
    }

    private void colourPrint(AnsiColour colour, String text) {
        System.out.println(colour.getCode() + text + AnsiColour.RESET.getCode());
    }

    private <T> T choose(String prompt, Map<String, T> options) {
        colourPrint(AnsiColour.CYAN, prompt);

        final var answer = scanNextLine();
        if (options.containsKey(answer)) {
            return options.get(answer);
        }

        final var invalidPrompt = "not a valid option, try again.";
        showError(invalidPrompt);

        return choose(prompt, options);
    }
}
