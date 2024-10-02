
package games;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class DatabaseOperations {
    
    private static final String URL = "jdbc:mysql://localhost:3306/games";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
      public static void saveUserToDatabase(String username, String password, JFrame frame) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "INSERT INTO users(username, password) VALUES (?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            int rowInserted = statement.executeUpdate();
            if (rowInserted > 0) {
                JOptionPane.showMessageDialog(frame, "Signup successful :)");
                // Redirect to the LoginForm
            } else {
                JOptionPane.showMessageDialog(frame, "Signup Failed.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database Error :( " + ex.getMessage());
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
      
      public static void authenticateUser(String username, String password, JFrame frame) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(frame, "Login successful :)");

                // Set the logged-in user in UserSession
                UserSession.getInstance().setUsername(username);
// Redirect to the MenuPage
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password :(");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database Error :( " + ex.getMessage());
        } finally {
            // Close resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
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
      
      public static void storeResult(int score, String game_name) {
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

    
}
