package spaceInvaders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PauseMenu extends JPanel {
    private JButton resumeButton;
    private JButton restartButton;
    private JButton exitButton;

    public interface PauseMenuListener {
        void onResume();
        void onRestart();
        void onExit();
    }

    public PauseMenu(PauseMenuListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(0, 0, 0, 0)); // Transparent background

        // Stretch to full screen
        setBounds(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);

        // Resume Button
        resumeButton = new JButton("Resume");
        resumeButton.setAlignmentX(CENTER_ALIGNMENT);
        resumeButton.addActionListener(e -> listener.onResume());
        add(resumeButton);

        // Restart Button
        restartButton = new JButton("Restart");
        restartButton.setAlignmentX(CENTER_ALIGNMENT);
        restartButton.addActionListener(e -> listener.onRestart());
        add(restartButton);

        // Exit Button
        exitButton = new JButton("Exit");
        exitButton.setAlignmentX(CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> listener.onExit());
        add(exitButton);

        setVisible(false); // Hidden by default
    }

    public void showMenu() {
        setVisible(true);
    }

    public void hideMenu() {
        setVisible(false);
    }
}
