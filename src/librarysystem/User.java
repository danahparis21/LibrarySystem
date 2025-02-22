package librarysystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    
    private int userID;
    private String role;

    // Constructor
    public User(int userID, String role) {
        this.userID = userID;
        this.role = role;
    }

    // Getters
    public int getUserID() {
        return userID;
    }

    public String getRole() {
        return role;
    }

    // Register a new user
    public static boolean signUp(String name, String email, String password, String address, String contact, String role) {
        String sql = "INSERT INTO Users (name, email, password, address, contact, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.connect(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, hashPassword(password)); // Hash the password
            stmt.setString(4, address);
            stmt.setString(5, contact);
            stmt.setString(6, role);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;  // Return true if registration is successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

     // Login method that fetches both userID and role
    public static User login(String name, String password) {
        String sql = "SELECT userID, role FROM Users WHERE name = ? AND password = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, hashPassword(password)); // Compare hashed password

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userID = rs.getInt("userID");  // Fetch userID
                String role = rs.getString("role").trim().toLowerCase();
                return new User(userID, role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Login failed
    }


}
