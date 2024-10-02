
package games.flappyBird;

import javax.swing.JFrame;


public class FlappyBirdGame {
    
    public FlappyBirdGame() {
        int boardWidth = 360;
        int boardHeight = 640;
        
        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(boardWidth, boardHeight);
        
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        FlappyBirdPanel flappyBirdPanel = new FlappyBirdPanel(frame);
        frame.add(flappyBirdPanel);
        flappyBirdPanel.requestFocus();
        frame.pack();
        frame.setVisible(true);
        
    }
}
