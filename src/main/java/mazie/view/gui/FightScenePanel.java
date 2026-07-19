package mazie.view.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import mazie.model.Hero;
import mazie.model.monster.Monster;

public class FightScenePanel extends JPanel {

    private final Image background;
    private final Image hero;

    public FightScenePanel(Monster monster, Hero hero) {
        setBorder(BorderFactory.createEmptyBorder());
        setOpaque(true);
        setPreferredSize(new Dimension(600, 400));
        background = PngMap.getPng(monster).getImage();
        this.hero = PngMap.getPng(hero.getType()).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        drawHero(g);
    }

    private void drawHero(Graphics g) {
        final var ogWidth = hero.getWidth(this);
        final var ogHeight = hero.getHeight(this);

        final var factor = calculateScale(ogWidth, ogHeight);

        final var heroWidth = factor * ogWidth;
        final var heroHeight = factor * ogHeight;

        final var heroX = (getWidth() - heroWidth) / 2;
        final var heroY = getHeight() - heroHeight - 10;

        g.drawImage(hero, (int) heroX, (int) heroY, (int) heroWidth, (int) heroHeight, this);
    }

    private double calculateScale(int width, int height) {
        final var maxWidth = getWidth() * 0.3;
        final var maxHeight = getHeight() * 0.6;

        final var widthFactor = maxWidth / width;
        final var heightFactor = maxHeight / height;

        return Math.min(widthFactor, heightFactor);
    }
}

