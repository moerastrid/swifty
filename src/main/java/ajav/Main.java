package ajav;

import ajav.model.HeroFactory;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello and welcome!");

        final var bear = HeroFactory.getInstance().newHero("BEAR", "Rico");
        final var hare = HeroFactory.getInstance().newHero("HARE", "Private");
        final var penguin = HeroFactory.getInstance().newHero("PENGUIN", "Skipper");
        final var turtle = HeroFactory.getInstance().newHero("TURTLE", "Kowalski");
        final var frog = HeroFactory.getInstance().newHero("FROG", "Greg");
        System.out.println("-------- start --------");
        for (int i = 1; i <= 12; i++) {

            System.out.println("xp += " + 500 * i);
            bear.gainExperience(500 * i);
            hare.gainExperience(500 * i);
            penguin.gainExperience(500 * i);
            turtle.gainExperience(500 * i);
            frog.gainExperience(500 * i);
        }
        System.out.println("-------- end --------");
        System.out.println(bear);
        System.out.println(hare);
        System.out.println(penguin);
        System.out.println(turtle);
        System.out.println(frog);
    }
}