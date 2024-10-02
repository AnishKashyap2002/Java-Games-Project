package games.spaceInvaders;

import games.DatabaseOperations;
import games.MenuPage;
import games.UserSession;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;


public class SpaceInvadersPanel extends JPanel implements ActionListener, KeyListener {

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

    int tileSize = 32;
    int rows = 16;
    int columns = 16;

    int boardWidth = tileSize * columns;
    int boardHeight = tileSize * rows;
    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;

    ArrayList<Image> alienImageArray;

    // ship
    int shipWidth = tileSize * 2;
    int shipHeight = tileSize;
    int shipX = boardWidth / 2 - tileSize;
    int shipY = boardHeight - tileSize * 2;
    int shipVelocityX = 32;

    // alien
    int alienVelocityY = 1;

    Block ship;

    // game timer
    Timer gameLoop;
    ArrayList<Block> aliens;
    int alienCount = 8;
    int level = 1;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;

    // Bullets 
    ArrayList<Block> bullets;
    int bulletWidth = tileSize / 8;
    int bulletHeight = tileSize / 2;
    int bulletVelocityY = -10;

    // score
    int score = 0;

    boolean gameOver = false;

    Random random = new Random();
    private JFrame frame;

    SpaceInvadersPanel(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);

        shipImg = new ImageIcon("ship.png").getImage();
        alienImg = new ImageIcon("alien.png").getImage();
        alienCyanImg = new ImageIcon("alien-cyan.png").getImage();
        alienMagentaImg = new ImageIcon("alien-magenta.png").getImage();
        alienYellowImg = new ImageIcon("alien-yellow.png").getImage();

        alienImageArray = new ArrayList<Image>();
        alienImageArray.add(alienImg);
        alienImageArray.add(alienCyanImg);
        alienImageArray.add(alienMagentaImg);
        alienImageArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);

        aliens = new ArrayList<>();
        createAliens();

        bullets = new ArrayList<>();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        addKeyListener(this);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(shipImg, ship.x, ship.y, ship.width, ship.height, null);

        for (int i = 0; i < aliens.size(); i++) {
            Block alien = aliens.get(i);
            g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
        }
        g.setColor(Color.white);
        for (int i = 0; i < bullets.size(); i++) {
            Block bullet = bullets.get(i);
            g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
        }

        // score
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        if(gameOver) {
            g.setFont(new Font("MV Boli", Font.BOLD, 50));
            g.setColor(Color.red);
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Over: " + String.valueOf((int) score), (boardWidth - metrics.stringWidth("Game Over: " + String.valueOf((int) score))) / 2, boardHeight / 2);

            g.setColor(Color.white);
            g.setFont(new Font("MV Boli", Font.BOLD, 32));
            metrics = getFontMetrics(g.getFont());
            g.drawString("Press R to restart", (boardWidth - metrics.stringWidth("Press R to restart")) / 2, 75);
            
            g.setColor(Color.white);
            g.setFont(new Font("MV Boli", Font.BOLD, 32));
            metrics = getFontMetrics(g.getFont());
            g.drawString("Press M for Menu", (boardWidth - metrics.stringWidth("Press M for Menu")) / 2, 110);
        }
        else {
            g.drawString("Score: " + String.valueOf(score), 10, 35);
        }

    }

    public void move() {
        for (int i = 0; i < aliens.size(); i++) {
            Block alien = aliens.get(i);
            alien.y += alienVelocityY;
            
            if(alien.y >= ship.y) {
                gameOver  = true;
            }

        }
        int aliensSize = aliens.size();
        for (int i = 0; i < bullets.size(); i++) {
            Block bullet = bullets.get(i);
            bullet.y += bulletVelocityY;

            for (int j = 0; j < aliensSize; j++) {
                Block alien = aliens.get(j);
                if (detectCollision(alien, bullet)) {
                    aliens.remove(alien);
                    bullets.remove(bullet);
                    score += 10;
                    break;

                }
            }
            if (bullet.y + bulletHeight < 0) {
                bullets.remove(bullet);
                i--;
            }
            if (aliensSize != aliens.size()) {
                i--;
            }
            aliensSize = aliens.size();

        }
        if (aliens.size() == 0) {
            score += 100;
            level++;
            bullets.clear();
            createAliens();
        }

    }

    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x
                && a.y < b.y + b.height && a.y + a.height > b.y;

    }

    public boolean alienAlreadyExists(Block newAlien) {
        for (int i = 0; i < aliens.size(); i++) {
            Block existingAlien = aliens.get(i);
            if (detectCollision(newAlien, existingAlien)) {
                return true;
            }
        }

        return false;

    }

    public void createAliens() {
        int noOfAliens = level * alienCount;
        for (int i = 0; i < noOfAliens; i++) {
            int randomIdx = random.nextInt(rows);
            int randomImgIndex = random.nextInt(alienImageArray.size());
            int x = randomIdx * tileSize;
            int y = 0;
            Block alien = new Block(x, y, alienWidth, alienHeight, alienImageArray.get(randomImgIndex));

            while (alienAlreadyExists(alien)) {
                y -= tileSize;
                alien.y = y;
            }
            if (alien.x + alienWidth >= boardWidth) {
                i--;
                continue;
            }

            aliens.add(alien);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        
        if(gameOver) {
            DatabaseOperations.storeResult(score, "Space Invaders");
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0) {
            ship.x -= shipVelocityX;

        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + ship.width < boardWidth) {
            ship.x += shipVelocityX;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            Block bullet = new Block(ship.x + shipWidth * 15 / 32, ship.y, bulletWidth, bulletHeight, null);
            bullets.add(bullet);
        }
        else if(e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            ship.x = shipX;
            aliens.clear();
            bullets.clear();
            score = 0;
            level = 1;
            gameOver = false;
            createAliens();
            gameLoop.start();
        }
        else if(e.getKeyCode() == KeyEvent.VK_M && gameOver) {
            frame.dispose();
            new MenuPage().setVisible(true);
            
        }
        
    }
    
    
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    
}
