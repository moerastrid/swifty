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
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setOpaque(true);
        this.setPreferredSize(new Dimension(600, 400));
        this.background = PngMap.getPng(monster).getImage();
        this.hero = PngMap.getPng(hero.getType()).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
        this.drawHero(g);
    }

    private void drawHero(Graphics g) {
        final var ogWidth = this.hero.getWidth(this);
        final var ogHeight = this.hero.getHeight(this);

        final var factor = calculateScale(ogWidth, ogHeight);

        final var heroWidth = factor * ogWidth;
        final var heroHeight = factor * ogHeight;

        final var heroX = (getWidth() - heroWidth) / 2;
        final var heroY = getHeight() - heroHeight - 10;

        g.drawImage(hero, (int) heroX, (int) heroY, (int) heroWidth, (int) heroHeight, this);
    }

    private double calculateScale(int width, int height) {
        final var maxWidth = this.getWidth() * 0.3;
        final var maxHeight = this.getHeight() * 0.6;

        final var widthFactor = maxWidth / width;
        final var heightFactor = maxHeight / height;

        return Math.min(widthFactor, heightFactor);
    }
}

