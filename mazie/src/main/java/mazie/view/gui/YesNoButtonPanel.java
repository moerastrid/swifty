package mazie.view.gui;

import java.awt.FlowLayout;
import java.util.concurrent.BlockingQueue;

import javax.swing.JButton;
import javax.swing.JPanel;

public class YesNoButtonPanel extends JPanel {

    public YesNoButtonPanel(BlockingQueue<Boolean> queue) {
        
        final var yesButton = new YesNoButton(true);
        final var noButton = new YesNoButton(false);
        
        setListener(yesButton, queue);
        setListener(noButton, queue);

        this.setLayout(new FlowLayout());
        this.add(yesButton);
        this.add(noButton);
    }

    private void setListener(YesNoButton button, BlockingQueue<Boolean> queue) {
        button.addActionListener(event -> {
            try {
                queue.put(button.getYes());
            }  catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("gui view interrupted: " + e.getMessage());
            }
        });
    }
}
