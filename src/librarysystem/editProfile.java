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
        setSize(420, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        connection = Database.connect();

        // Main Panel
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("Edit Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Labels & Text Fields
        gbc.gridwidth = 1;
        String[] labels = {"Name:", "Email:", "Address:", "Contact:", "Bio:"};
        JTextField[] fields = {nameField = new JTextField(), emailField = new JTextField(),
                               addressField = new JTextField(), contactField = new JTextField()};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            panel.add(label, gbc);

            gbc.gridx = 1;
            if (i < 4) {
                fields[i].setFont(new Font("Arial", Font.PLAIN, 14));
                fields[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                fields[i].setPreferredSize(new Dimension(200, 30));
                panel.add(fields[i], gbc);
            } else {
                bioField = new JTextArea(3, 20);
                bioField.setFont(new Font("Arial", Font.PLAIN, 14));
                bioField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                bioField.setLineWrap(true);
                bioField.setWrapStyleWord(true);
                JScrollPane bioScroll = new JScrollPane(bioField);
                bioScroll.setPreferredSize(new Dimension(200, 80));
                panel.add(bioScroll, gbc);
            }
        }

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        JButton[] buttons = {saveButton, cancelButton};
        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setPreferredSize(new Dimension(120, 35));
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(70, 130, 180));

            // Hover Effect
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(70, 130, 180));
                    button.setForeground(Color.WHITE);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(Color.WHITE);
                    button.setForeground(new Color(70, 130, 180));
                }
            });
        }

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = labels.length + 1;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        // Button Actions
        saveButton.addActionListener(e -> updateProfile());
        cancelButton.addActionListener(e -> dispose());

        add(panel);
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
