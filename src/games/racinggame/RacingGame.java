package games.racinggame;

import games.MenuPage;
import games.UserSession;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RacingGame extends JFrame {

    private JLabel countdownLabel;
    private JTextArea typingArea;
    private JLabel wpmLabel;
    private JLabel wordLabel;
    private Timer raceTimer;
    private int timeRemaining;
    private List<String> words;
    private int wordIndex;
    private int correctWordCount;
    private long startTime;
    private String currentWord;
    private JLabel[] nextWords = new JLabel[20];

    public RacingGame() {
        setTitle("Typing Race Game");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize word list with a larger set of words
         words = new ArrayList<>();
        Collections.addAll(words,
            "apple", "banana", "cherry", "date", "elderberry", "fig", "grape", "honeydew", "kiwi", "lemon",
            "mango", "nectarine", "orange", "pear", "quince", "raspberry", "strawberry", "tangerine", "ugli", "vine",
            "watermelon", "xylophone", "yellow", "zucchini", "ant", "beetle", "cat", "dog", "elephant", "frog",
            "giraffe", "horse", "iguana", "jaguar", "kangaroo", "lion", "monkey", "narwhal", "octopus", "penguin",
            "quail", "rabbit", "snake", "tiger", "umbrella", "vulture", "wolf", "xerox", "yak", "zebra",
            "airport", "beach", "city", "doghouse", "eggplant", "fire", "garden", "hotel", "island", "jungle",
            "keyhole", "library", "mountain", "night", "ocean", "puzzle", "quarantine", "river", "sun", "tree",
            "unicorn", "village", "window", "xenon", "yacht", "zoo", "airplane", "bridge", "computer", "dragon",
            "envelope", "flower", "grapes", "house", "internet", "jacket", "kitchen", "lighthouse", "moon", "newspaper"
        );

        Collections.shuffle(words); // Shuffle words for randomness
        wordIndex = 0;
        correctWordCount = 0;

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.black);
        countdownLabel = new JLabel("Time Remaining: 30 seconds", JLabel.CENTER);
        countdownLabel.setFont(new Font("MV Boli", Font.BOLD, 24));
        countdownLabel.setForeground(Color.decode("#e7eff6"));
        panel.add(countdownLabel, BorderLayout.NORTH);

        JPanel wordsPanel = new JPanel(new FlowLayout());
        wordsPanel.setBackground(Color.black);
        wordsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wordLabel = new JLabel(words.get(wordIndex), JLabel.CENTER);
        wordLabel.setFont(new Font("MV Boli", Font.BOLD, 30));
        wordLabel.setForeground(Color.decode("#a0d2eb"));
        wordLabel.setBackground(Color.black);
        wordLabel.setOpaque(true);
        wordsPanel.add(wordLabel);
        for (int i = 0; i < 20; i++) {
            nextWords[i] = new JLabel(words.get(wordIndex + i + 1));
            nextWords[i].setFont(new Font("MV Boli", Font.PLAIN,20));
            nextWords[i].setForeground(Color.decode("#bbbbbb"));

            wordsPanel.add(nextWords[i]);

        }

        panel.add(wordsPanel, BorderLayout.CENTER);

        typingArea = new JTextArea();
        typingArea.setFont(new Font("MV Boli", Font.PLAIN, 24));
        typingArea.setLineWrap(true);
        
        typingArea.setBackground(Color.decode("#a8e6cf"));
        typingArea.setWrapStyleWord(true);
//        typingArea.setPreferredSize(new Dimension(800, 50));
//        typingArea.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
        typingArea.setMargin(new Insets(10, 10, 10, 10));
       
//        JScrollPane scrollPane = new JScrollPane(typingArea);
//        panel.add(scrollPane, BorderLayout.SOUTH);
//typingArea.setHorizontal
        panel.add(typingArea, BorderLayout.SOUTH);

        wpmLabel = new JLabel("WPM: 0", JLabel.CENTER);
        wpmLabel.setFont(new Font("Arial", Font.BOLD, 24));
//        panel.add(wpmLabel, BorderLayout.WEST);

        add(panel);

        // Initialize the countdown timer for 30 seconds
        timeRemaining = 30;
        raceTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeRemaining > 0) {
                    countdownLabel.setText("Time Remaining: " + timeRemaining + " seconds");
                    timeRemaining--;
                } else {
                    ((Timer) e.getSource()).stop();
                    endRace(); // Ensure endRace() is called when the timer reaches 0
                }
            }
        });
        raceTimer.start();

        startTime = System.currentTimeMillis();
        currentWord = words.get(wordIndex);
        wordLabel.setText(currentWord);

        // Add a key listener to the text area to check the input as the user types
        typingArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkInput();
            }
        });

        setVisible(true);
    }

    private boolean checkSubstring(String str, String sub) {
        if (sub.length() > str.length()) {
            return false;
        }
        int i = 0;
        while (i < sub.length() && str.charAt(i) == sub.charAt(i)) {
            i++;
        }
        return i == sub.length();
    }

    private void checkInput() {
        String typedText = typingArea.getText().trim();
        if (typedText.equals(currentWord)) {
            correctWordCount++;
            wordIndex++;
            if (wordIndex < words.size()) {
                currentWord = words.get(wordIndex);
                System.out.println(wordIndex);
                for (int i = 0; i < 20; i++) {
                    nextWords[i].setText(words.get(wordIndex + i + 1));
                }
                wordLabel.setText(currentWord);
                typingArea.setText("");

                // Clear the text area for the next word
            } else {
                // If all words are completed before time runs out
                endRace();
            }
        } else {
            if (checkSubstring(currentWord, typedText)) {
//                wordLabel.setForeground(Color.green);
//wordLabel.setBackground(Color.decode("#51e2f5"));
        wordLabel.setForeground(Color.decode("#a0d2eb"));
        typingArea.setBackground(Color.decode("#a8e6cf"));

            } else {
                wordLabel.setForeground(Color.red);
                typingArea.setBackground(Color.decode("#ffd3b6"));
            }
        }
    }

    private void endRace() {
        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;
        double timeInMinutes = (double) timeTaken / 60000.0;
        int wpm = (int) ((correctWordCount / timeInMinutes));

        wpmLabel.setText("WPM: " + wpm);

        JOptionPane.showMessageDialog(this, "Race complete! Your WPM: " + wpm);

        // Store result in the database
        storeResult(wpm, "Type Racing");

        // Return to MenuPage
        dispose();
        new MenuPage().setVisible(true);
    }

    private void storeResult(int score, String game_name) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/games", "root", "root");

            String query = "INSERT INTO user_scores (player_name, game, score) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, UserSession.getInstance().getUsername()); // Fetch the username from UserSession
            statement.setString(2, game_name);
            statement.setInt(3, score);

            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RacingGame::new);
    }
}
