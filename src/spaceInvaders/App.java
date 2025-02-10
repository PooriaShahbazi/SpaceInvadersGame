package spaceInvaders;

import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SpaceInvaders spaceInvaders = new SpaceInvaders();
        frame.add(spaceInvaders);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        frame.setUndecorated(true); // Remove title bar
        frame.setVisible(true);

        spaceInvaders.requestFocus();
    }
}