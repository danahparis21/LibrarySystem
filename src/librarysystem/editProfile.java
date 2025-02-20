package librarysystem;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class editProfile extends JFrame {
    private int userID;
    private Connection connection;
    private JTextField nameField, emailField, addressField, contactField;
    private JTextArea bioField;
    private JButton saveButton, cancelButton;

    public editProfile(int userID) {
        this.userID = userID;
        setTitle("Edit Profile");
        setSize(400, 450);
        setLayout(null); // Using null layout
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        connection = Database.connect();

        // Labels
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(30, 30, 100, 25);
        add(nameLabel);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(30, 70, 100, 25);
        add(emailLabel);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(30, 110, 100, 25);
        add(addressLabel);

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setBounds(30, 150, 100, 25);
        add(contactLabel);

        JLabel bioLabel = new JLabel("Bio:");
        bioLabel.setBounds(30, 190, 100, 25);
        add(bioLabel);

        // Text Fields
        nameField = new JTextField();
        nameField.setBounds(130, 30, 200, 25);
        add(nameField);

        emailField = new JTextField();
        emailField.setBounds(130, 70, 200, 25);
        add(emailField);

        addressField = new JTextField();
        addressField.setBounds(130, 110, 200, 25);
        add(addressField);

        contactField = new JTextField();
        contactField.setBounds(130, 150, 200, 25);
        add(contactField);

        // Multi-line Bio Field (supports emojis, nullable)
        bioField = new JTextArea();
        bioField.setLineWrap(true);
        bioField.setWrapStyleWord(true);
        JScrollPane bioScroll = new JScrollPane(bioField); // Scrollable
        bioScroll.setBounds(130, 190, 200, 80);
        add(bioScroll);

        // Buttons
        saveButton = new JButton("Save");
        saveButton.setBounds(80, 300, 100, 30);
        saveButton.addActionListener(e -> updateProfile());
        add(saveButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(200, 300, 100, 30);
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        loadUserData();
        setVisible(true);
    }

    private void loadUserData() {
        try {
            String query = "SELECT name, email, address, contact, bio FROM Users WHERE userID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                addressField.setText(rs.getString("address"));
                contactField.setText(rs.getString("contact"));
                
                // Handle NULL bio (set empty string if null)
                String bioText = rs.getString("bio");
                bioField.setText(bioText != null ? bioText : "");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProfile() {
        try {
            String query = "UPDATE Users SET name = ?, email = ?, address = ?, contact = ?, bio = ? WHERE userID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, nameField.getText());
            stmt.setString(2, emailField.getText());
            stmt.setString(3, addressField.getText());
            stmt.setString(4, contactField.getText());

            // Save NULL if bio is empty
            String bioText = bioField.getText().trim();
            if (bioText.isEmpty()) {
                stmt.setNull(5, Types.VARCHAR); 
            } else {
                stmt.setString(5, bioText);
            }

            stmt.setInt(6, userID);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            stmt.close();
            dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating profile!");
        }
    }
}
