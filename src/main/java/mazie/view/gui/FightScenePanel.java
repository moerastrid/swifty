package mazie.view.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import mazie.model.Hero;
import mazie.model.monster.Monster;

public class FightScenePanel extends JPanel {

    private final Image background;
    private final Image hero;

    public FightScenePanel(Monster monster, Hero hero) {
        this.setOpaque(true);
        this.setPreferredSize(new Dimension(600, 400));
        this.background = PngMap.getPng(monster).getImage();
        this.hero = PngMap.getPng(hero).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(background, 0, 0,
                getWidth(), getHeight(), this);

        final var ogWidth = this.hero.getWidth(this);
        final var ogHeight = this.hero.getHeight(this);
        final var heroHeight = getHeight() * 0.4;

        final var factor = heroHeight / ogHeight;
        final var heroWidth = factor * ogWidth;

        final var heroX = (getWidth() - heroWidth) / 2;
        final var heroY = getHeight() - heroHeight - 10;

        g.drawImage(hero, (int) heroX, (int) heroY, (int) heroHeight, (int ) heroWidth, this);
    }
}

