package games;

public class UserSession {
    private static UserSession instance;
    private String username = "Anish";

    // Private constructor to prevent instantiation
    private UserSession() {}

    // Singleton pattern to ensure only one instance exists
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Set the username of the logged-in user
    public void setUsername(String username) {
        this.username = username;
    }

    // Get the username of the logged-in user
    public String getUsername() {
        return username;
    }

    // Clear the session (e.g., on logout)
    public void clearSession() {
        username = null;
        instance = null;
    }
}
