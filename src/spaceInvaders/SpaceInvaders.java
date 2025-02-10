package spaceInvaders;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {
    int boardWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    int boardHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    int tileSize = boardHeight / 16;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    Image backgroundImg;
    ArrayList<Image> alienImgArray;

    String highScoreFileName = "highscore.txt";
    ArrayList<Integer> highScores = new ArrayList<>(5);

    PauseMenu pauseMenu;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true;
        boolean used = false;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    int shipWidth = tileSize * 4;
    int shipHeight = tileSize * 3;
    int shipX = boardWidth / 2 - shipWidth / 2;
    int shipY = boardHeight - tileSize * 4;
    int shipVelocityX = tileSize;
    Block ship;

    ArrayList<Block> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize * 2;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienColumns = 3;
    int alienCount = 0;
    int alienVelocityX = 1;

    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize / 8;
    int bulletHeight = tileSize / 4;
    int bulletVelocityY = -20;

    Timer gameLoop;
    boolean gameOver = false;
    boolean paused = false;
    int score = 0;
    int highScore = 0;

    SpaceInvaders() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        loadHighScores();

        backgroundImg = new ImageIcon(getClass().getResource("/resources/background.png")).getImage();
        shipImg = new ImageIcon(getClass().getResource("/resources/ship.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("/resources/alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("/resources/alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("/resources/alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("/resources/alien-yellow.png")).getImage();

        alienImgArray = new ArrayList<>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        gameLoop = new Timer(1000 / 60, this);

        pauseMenu = new PauseMenu(new PauseMenu.PauseMenuListener() {
            @Override
            public void onResume() {
                paused = false;
                pauseMenu.hideMenu();
                requestFocusInWindow();
            }

            @Override
            public void onRestart() {
                restartGame();
                pauseMenu.hideMenu();
            }

            @Override
            public void onExit() {
                System.exit(0);
            }
        });
        add(pauseMenu);

        createAliens();
        gameLoop.start();
    }

    public void restartGame() {
        // Reset game variables
        ship.x = shipX;
        bulletArray.clear();
        alienArray.clear();
        gameOver = false;
        paused = false;
        score = 0;
        alienColumns = 3;
        alienRows = 2;
        alienVelocityX = 1;

        // Recreate aliens
        createAliens();

        // Restart the game loop
        gameLoop.stop(); // Stop any existing timers
        gameLoop.start();

        // Refocus the game panel
        requestFocusInWindow();
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        if (paused) {
            return; // Pause menu is rendered separately
        }

        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        for (Block alien : alienArray) {
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        g.setColor(Color.green);
        for (Block bullet : bulletArray) {
            if (!bullet.used) {
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        g.setFont(new Font("Arial", Font.BOLD, 16)); // Set font for readability
        g.drawString("Score: " + score, 10, 35);

        // Display top 5 high scores on the top right corner
        int xOffset = boardWidth - 200; // Adjust for the right corner
        int yOffset = 70; // Starting position for high scores
        g.drawString("Top 5 High Scores:", xOffset, yOffset);

        for (int i = 0; i < highScores.size(); i++) {
            g.drawString((i + 1) + ". " + highScores.get(i), xOffset, yOffset + 20 + (i * 20));
        }

        if (gameOver) {
            g.setFont(new Font("Arial", Font.PLAIN, 64));
            g.drawString("Game Over", boardWidth / 2 - 200, boardHeight / 2 - 50);
            g.drawString("Score: " + score, boardWidth / 2 - 200, boardHeight / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!paused && !gameOver) {
            move();
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (!paused) {
                paused = true;
                pauseMenu.showMenu();
            }
        } else if (!paused && !gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0) {
                ship.x -= shipVelocityX;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + shipVelocityX + ship.width <= boardWidth) {
                ship.x += shipVelocityX;
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                Block bullet = new Block(ship.x + shipWidth * 15 / 32, ship.y, bulletWidth, bulletHeight, null);
                bulletArray.add(bullet);
            }
        }

        if (gameOver && e.getKeyCode() != KeyEvent.VK_ESCAPE) {
            restartGame();
        }
    }

    public void move() {
        if (paused || gameOver) return;

        for (Block alien : alienArray) {
            if (alien.alive) {
                alien.x += alienVelocityX;

                if (alien.x + alien.width >= boardWidth || alien.x <= 0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX * 2;

                    for (Block otherAlien : alienArray) {
                        otherAlien.y += alienHeight;
                    }
                }

                if (alien.y >= ship.y) {
                    gameOver = true;
                    saveHighScores();
                }
            }
        }

        for (Block bullet : bulletArray) {
            bullet.y += bulletVelocityY;

            for (Block alien : alienArray) {
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    bullet.used = true;
                    alien.alive = false;
                    alienCount--;
                    score += 100;
                }
            }
        }

        bulletArray.removeIf(bullet -> bullet.used || bullet.y < 0);

        if (alienCount == 0) {
            score += alienColumns * alienRows * 100;
            alienColumns = Math.min(alienColumns + 1, 8);
            alienRows = Math.min(alienRows + 1, 4);
            alienArray.clear();
            bulletArray.clear();
            createAliens();
        }
    }

    public void createAliens() {
        Random random = new Random();
        for (int c = 0; c < alienColumns; c++) {
            for (int r = 0; r < alienRows; r++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                    alienX + c * alienWidth,
                    alienY + r * alienHeight,
                    alienWidth,
                    alienHeight,
                    alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    public void loadHighScores() {
        highScores.clear();
        try (BufferedReader inputStream = new BufferedReader(new FileReader(highScoreFileName))) {
            String line;
            while ((line = inputStream.readLine()) != null) {
                try {
                    int score = Integer.parseInt(line.trim());
                    highScores.add(score);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid score format: " + line);
                }
            }
            highScores.sort((a, b) -> b - a); // Sort descending
            while (highScores.size() > 5) highScores.remove(highScores.size() - 1);
        } catch (IOException e) {
            System.err.println("Could not load high scores: " + e.getMessage());
        }
    }

    public void saveHighScores() {
        if (!highScores.contains(score)) {
            highScores.add(score);
            highScores.sort((a, b) -> b - a); // Sort descending
            while (highScores.size() > 5) highScores.remove(highScores.size() - 1);
        }
        try (PrintWriter outputStream = new PrintWriter(new FileWriter(highScoreFileName))) {
            for (int s : highScores) {
                outputStream.println(s);
            }
        } catch (IOException e) {
            System.err.println("Could not save high scores: " + e.getMessage());
        }
    }
}
