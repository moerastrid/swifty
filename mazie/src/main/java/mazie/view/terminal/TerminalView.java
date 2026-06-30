package mazie.view.terminal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import mazie.exception.QuitException;
import mazie.model.Artifact;
import mazie.model.Direction;
import mazie.model.Hero;
import mazie.model.HeroType;
import mazie.model.Monster;
import mazie.view.GameView;

public class TerminalView implements GameView {

    private final Scanner scanner;
    private final static Map<String, Boolean> YES_NO = Map.of(
            "y", true,
            "yes", true,
            "n", false,
            "no", false);

    public TerminalView() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void showError(String error) {
        colorPrint(AnsiColor.YELLOW, error);
    }

    @Override
    public void showWelcome() {
        try (var inputstream = getClass().getResourceAsStream("/mazie-icon-ascii.txt")) {
            String content = new String(inputstream.readAllBytes());
            System.out.print(content);
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
			- bear (b)
			- frog (f)
			- hare (h)
		""";

        final var options = new HashMap<String, HeroType>();
        options.put("b", HeroType.BEAR);
        options.put("bear", HeroType.BEAR);
        options.put("f", HeroType.FROG);
        options.put("frog", HeroType.FROG);
        options.put("h", HeroType.HARE);
        options.put("hare", HeroType.HARE);

        HeroType type = choose(typePrompt, options);

        final var namePrompt = "Name your %s: ".formatted(type);
        colorPrint(AnsiColor.PURPLE, namePrompt);

        return new Hero(scanNextLine(), type);
    }

    @Override
    public Hero selectHero(List<Hero> heroes) {
		if (heroes.isEmpty()) {
			return null;
		}
		final var prompt = "choose your fighter!\n(id/name)";
		final var options = new HashMap<String, Hero>();
		heroes.stream().forEach(hero -> {
			showHeroStats(hero);
			options.put(hero.getName(), hero);
			// #todo when persistent options.put(Integer.toString(hero.getId()), hero);
		});

        return choose(prompt, options);
    }

    @Override
    public boolean confirmHero(Hero hero) {
        final var prompt = "Do you want to start your journey with %s? (y/n)".formatted(hero.getName());
        return choose(prompt, YES_NO);
    }

    @Override
    public void showHeroStats(Hero hero) {

        final var artifacts = hero.getArtifacts().isEmpty() ? "nothing" : String.join("\n- ", hero.getArtifacts().stream().map(a -> a.toString()).toList());

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
    public Direction askDirection() {
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
    public boolean askFightMonster(Monster monster) {
        final var prompt = "Straight from the ghetto, something walks towards you..\n%s\nAre you bout that action? (y/n)".formatted(monster.toString());
        return choose(prompt, YES_NO);
    }

    @Override
    public void showRunSuccess(Monster monster, boolean success) {
        String prompt;

        if (success) {
            prompt = "You've escaped! That %s got nothing on you!".formatted(monster.getName());
        } else {
            prompt = """
				Before sneaking away, you lock eyes with the monster.
				You realize, this %s makes you feel weak in the knees.
				Your heart starts beating faster. There's no escape now..."
			""".formatted(monster.getName());
        }
        colorPrint(AnsiColor.PURPLE, prompt);
    }

    @Override
    public void showEndGame(boolean win) {
        String prompt;

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
    public void showFightSummary(String fightSummary, int xpGained) {
        final var prompt = """
			fight summary: %s
			XP gained: %s
		""".formatted(fightSummary, xpGained);
        colorPrint(AnsiColor.BLUE, prompt);
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

        final var artifacts = hero.getArtifacts().isEmpty() ? "nothing" : String.join("\n- ", hero.getArtifacts().stream().map(a -> a.toString()).toList());

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

    private String scanNextLine() throws QuitException {
        try {
            String s = scanner.nextLine();
            return s.strip().toLowerCase();
        } catch (NoSuchElementException e) {
            showError("?user entered ^C or ^D in terminal?" + e);
            throw new QuitException("?user entered ^C or ^D in terminal?", e);
        }
    }

    private <T> T choose(String prompt, Map<String, T> options) {
        colorPrint(AnsiColor.CYAN, prompt);

        final var answer = scanNextLine();
        if (options.containsKey(answer)) {
            return options.get(answer);
        }

        final var invalidPrompt = "not a valid choise, try again.";
        showError(invalidPrompt);

        return choose(prompt, options);
    }
}
