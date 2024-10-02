
package games.spaceInvaders;

import java.awt.Dimension;
import javax.swing.JFrame;

public class SpaceInvaders {
    
    public SpaceInvaders() {
        int tileSize = 32;
        int rows = 16;
        int columns = 16;
        
        int boardWidth = tileSize * columns;
        int boardHeight = tileSize * rows;
        
        JFrame frame = new JFrame();
        frame.setTitle("Space Invaders");
        
        
        frame.setSize(boardWidth, boardHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        SpaceInvadersPanel panel = new SpaceInvadersPanel(frame);
        frame.add(panel);
        
        panel.requestFocus();
        
        frame.pack();
        frame.setVisible(true);
        
        
        
    }
    
    public static void main(String args[]) {
        new SpaceInvaders();
    }
}
