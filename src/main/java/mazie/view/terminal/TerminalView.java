package mazie.view.terminal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import mazie.exception.QuitException;
import mazie.exception.SwitchViewException;
import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.HeroType;
import mazie.model.monster.Monster;
import mazie.view.GameView;

public class TerminalView implements GameView {

    private final Scanner scanner;
    private Runnable switchListener;
    private final static Map<String, Boolean> YES_NO = Map.of(
            "y", true,
            "yes", true,
            "n", false,
            "no", false);

    public TerminalView() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void close() {
        // nothing (terminal stays open)
    }

    @Override
    public void setSwitchListener(Runnable switchListener) {
        this.switchListener = switchListener;
    }

    @Override
    public void showError(String error) {
        colorPrint(AnsiColor.YELLOW, error);
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
                	- weevil (w)
                	- frog (f)
                	- mouse (m)
                """;

        final var options = new HashMap<String, HeroType>();
        options.put("w", HeroType.WEEVIL);
        options.put("weevil", HeroType.WEEVIL);
        options.put("f", HeroType.FROG);
        options.put("frog", HeroType.FROG);
        options.put("m", HeroType.MOUSE);
        options.put("mouse", HeroType.MOUSE);
        // #todo waarom doe je dit hier handmatig putten? HeroType.values()

        HeroType type = choose(typePrompt, options);

        final var namePrompt = "Name your %s: ".formatted(type);
        colorPrint(AnsiColor.PURPLE, namePrompt);

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

        final var artifacts = hero.getArtifacts().isEmpty() ? "nothing" : String.join("\n- ", hero.getArtifacts().stream().map(Artifact::toString).toList());

        final var stats = """
                Hero(#%d) %s identifies as a %s,
                  lvl     :%d
                  xp      :%d
                  attack  :%d  (total %d)
                  defence :%d  (total %d)
                  hp      :%d  (total %d)
                and is wearing:
                - %s
                """.formatted(hero.getId(), hero.getName(), hero.getType().toString().toLowerCase(),
                hero.getLevel(),
                hero.getXp(),
                hero.getAttack(), hero.getTotalAttack(),
                hero.getDefence(), hero.getTotalDefence(),
                hero.getHp(), hero.getTotalHp(),
                artifacts);

        colorPrint(AnsiColor.PURPLE, stats);
    }

    @Override
    public void showStartGame() {
        final var prompt = """
                
                      ╔═╝╔═ ═╔╝╔═╝╔═║  ═╔╝║ ║╔═╝  ╔╔ ╔═║══║╝╔═╝
                      ╔═╝║ ║ ║ ╔═╝╔╔╝   ║ ╔═║╔═╝  ║║║╔═║╔╝ ║╔═╝
                      ══╝╝ ╝ ╝ ══╝╝ ╝   ╝ ╝ ╝══╝  ╝╝╝╝ ╝══╝╝══╝
                """;
        colorPrint(AnsiColor.BLUE, prompt);
    }

    @Override
    public Direction askDirection(Hero hero) {
        this.showHeroStats(hero);
        final var prompt = "where to go? (north/east/south/west)";
        final var options = new HashMap<String, Direction>();

        options.put("n", Direction.NORTH);
        options.put("north", Direction.NORTH);
        options.put("e", Direction.EAST);
        options.put("east", Direction.EAST);
        options.put("s", Direction.SOUTH);
        options.put("south", Direction.SOUTH);
        options.put("w", Direction.WEST);
        options.put("west", Direction.WEST);

        return choose(prompt, options);
    }

    @Override
    public void showEmptyStep() {
        colorPrint(AnsiColor.PURPLE, "one step forward!");
    }

    @Override
    public boolean wantToFightMonster(Hero hero, Monster monster) {
        this.showHeroStats(hero);
        final var prompt = "Straight from the ghetto, something walks towards you..\n%s\nAre you bout that action? (y/n)".formatted(monster.toString());
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
        colorPrint(AnsiColor.PURPLE, prompt);
    }

    @Override
    public void showEndGame(Hero hero, boolean win) {
        String prompt;
        this.showHeroStats(hero);

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
        colorPrint(AnsiColor.RED, prompt);
    }

    @Override
    public void showFightSummary(int damageToHero, Hero hero, Monster monster) {
        final var startSummary = "--- FIGHT SUMMARY ---";
        final var endSummary = "---------------------";
        final var damageSummary = "- %d hp     + %d xp".formatted(damageToHero, monster.getXpReward());


        colorPrint(AnsiColor.BLUE, startSummary);
        colorPrint(AnsiColor.GREEN, EmojiMap.getEmoji(hero.getType()) + hero.getAction());
        colorPrint(AnsiColor.PURPLE, EmojiMap.getEmoji(monster) + monster.getAction());
        colorPrint(AnsiColor.BLUE, damageSummary);
        colorPrint(AnsiColor.GREEN, monster.getFinalMessage());
        colorPrint(AnsiColor.BLUE, endSummary);
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

        colorPrint(AnsiColor.GREEN, prompt);
        this.showHeroStats(hero);
    }

    @Override
    public boolean askKeepArtifact(Artifact artifact, Hero hero) {

        final var artifacts = hero.getArtifacts().isEmpty() ? "nothing" : String.join("\n- ", hero.getArtifacts().stream().map(Artifact::toString).toList());

        final var prompt = """
                	litty, an artifact!
                	%s
                	Do you want to keep it? (y/n)
                	You're currently wearing:
                	- %s
                """.formatted(artifact.toString(), artifacts);

        return choose(prompt, YES_NO);
    }

    private void colorPrint(AnsiColor color, String text) {
        System.out.println(color + text + AnsiColor.RESET);
    }

    private String scanNextLine() throws QuitException, SwitchViewException {
        try {
            final var line = scanner.nextLine();
            final var answer = line.strip().toLowerCase();
            if (answer.equals("q") || answer.equals("quit") || answer.equals("exit")) {
                throw new QuitException("user is a quitter\ndeveloper is disappointed");
            } else if (answer.equals("switch")) {
                this.switchView();
            }
            return answer;
        } catch (NoSuchElementException e) {
            showError("?user entered ^C or ^D in terminal?");
            throw new QuitException("?user entered ^C or ^D in terminal?", e);
        }
    }

    private void switchView() {
        this.switchListener.run();
        throw new SwitchViewException();
    }

    private <T> T choose(String prompt, Map<String, T> options) {
        colorPrint(AnsiColor.CYAN, prompt);

        final var answer = scanNextLine();
        if (options.containsKey(answer)) {
            return options.get(answer);
        }

        final var invalidPrompt = "not a valid option, try again.";
        showError(invalidPrompt);

        return choose(prompt, options);
    }
}
