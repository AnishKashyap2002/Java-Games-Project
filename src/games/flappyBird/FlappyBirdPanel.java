package games.flappyBird;

import games.DatabaseOperations;
import games.MenuPage;
import games.UserSession;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FlappyBirdPanel extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    Image backgroundImg, birdImg, topPipeImg, bottomPipeImg;

    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {

        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {

        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    Bird bird;
    Timer gameLoop;
    Timer placePipesTimer;
    int velocityY = 0;
    int velocityX = -4;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random;

    double score = 0;

    boolean gameOver = false;
    
    private JFrame frame;
    FlappyBirdPanel(JFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon("flappybirdbg.png").getImage();
        birdImg = new ImageIcon("flappybird.png").getImage();
        topPipeImg = new ImageIcon("toppipe.png").getImage();
        bottomPipeImg = new ImageIcon("bottompipe.png").getImage();

        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        placePipesTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        Random random = new Random();

    }

    public void placePipes() {
        Pipe topPipe = new Pipe(topPipeImg);
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setFont(new Font("MV Boli", Font.BOLD, 32));
        g.setColor(Color.white);
        if (gameOver) {
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

        } else {
            g.drawString("Score: " + String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
            
        }

    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x
                && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            DatabaseOperations.storeResult((int)score, "Flappy Bird");
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
        }
        if(e.getKeyCode() == KeyEvent.VK_R) {
            if(gameOver) {
                int velocityY = 0;
                gameOver = false;
                score = 0;
                velocityY = 0;
                bird.y = birdY;
                pipes.clear();
                gameLoop.start();
                placePipesTimer.start();
            }
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
