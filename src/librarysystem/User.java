package librarysystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {

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

    // Hash password using SHA-256
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

    public static String login(String name, String password) {
     String sql = "SELECT role FROM Users WHERE name = ? AND password = ?";

     try (Connection conn = Database.connect();
          PreparedStatement stmt = conn.prepareStatement(sql)) {

         stmt.setString(1, name);
         stmt.setString(2, hashPassword(password)); // Compare hashed password

         ResultSet rs = stmt.executeQuery();
         if (rs.next()) {
            return rs.getString("role").trim().toLowerCase(); // Normalize the role for comparison
            
         } else {
             System.out.println("No matching user found for username: " + name);
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return null; // Login failed
 }

}
