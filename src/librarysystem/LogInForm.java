package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LogInForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signUpButton;
    private JLabel background;
    private ImageIcon[] bgImages;
    private int imageIndex = 0;
    private Timer backgroundTimer;

    public LogInForm() {
        setTitle("Library System - Login");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        // 🌟 Background Images
        bgImages = new ImageIcon[]{
            new ImageIcon(getClass().getResource("/icons/librarybg1.png")),
            new ImageIcon(getClass().getResource("/icons/librarybg2.png")),
            new ImageIcon(getClass().getResource("/icons/librarybg3.png"))
        };

        background = new JLabel(bgImages[0]);
        background.setBounds(0, 0, getWidth(), getHeight());
        getContentPane().add(background);

        // 🕒 Background Image Slideshow
        backgroundTimer = new Timer(5000, e -> {
            imageIndex = (imageIndex + 1) % bgImages.length;
            updateBackground();
        });
        backgroundTimer.start();

        // 🎨 Login Panel
        JPanel loginPanel = new JPanel();
        loginPanel.setBounds((getWidth() - 400) / 2, (int) (getHeight() * 0.55), 100, 100);
        loginPanel.setBackground(new Color(255, 255, 255, 200));
        loginPanel.setLayout(null);
        background.add(loginPanel);

        // 📌 Label & Input Fields
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 40, 100, 30);
        loginPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(50, 70, 300, 35);
        loginPanel.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 120, 100, 30);
        loginPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 150, 300, 35);
        loginPanel.add(passwordField);

        // ✅ Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(50, 200, 140, 40);
        styleButton(loginButton);
        loginPanel.add(loginButton);

        // 🚀 Sign Up Button
        signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(210, 200, 140, 40);
        styleButton(signUpButton);
        loginPanel.add(signUpButton);

        // 🎯 Login Action
        loginButton.addActionListener(e -> {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = User.login(username, password); // Fetch user details

        if (user == null) {
            JOptionPane.showMessageDialog(null, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            int userID = user.getUserID();  // Get the user ID
            String role = user.getRole();

            if (role.equals("staff")) {
                new StaffInterface().setVisible(true);
                dispose();
            } else if (role.equals("member")) {
                new MemberDashboard(userID).setVisible(true); // Pass userID to MemberDashboard
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Unexpected error.");
            }
        }
    });

        signUpButton.addActionListener(e -> {
            new SignupForm().setVisible(true);
            dispose();
        });

        setVisible(true);
        resizeBackground();
    }

    // 🎨 Button Styling
    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setBackground(new Color(186, 156, 96));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(110, 80, 34), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // 🖼️ Update Background Image
    private void updateBackground() {
        int width = getWidth();
        int height = getHeight();
        Image scaledImage = bgImages[imageIndex].getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        background.setIcon(new ImageIcon(scaledImage));
        background.setBounds(0, 0, width, height);
    }

     private void resizeBackground() {
        int width = getWidth();
        int height = getHeight();

        // Scale image smoothly
       Image scaledImage = bgImages[imageIndex].getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        background.setIcon(new ImageIcon(scaledImage));
        background.setBounds(0, 0, width, height);

        // Reposition login panel
        Component[] components = background.getComponents();
        // Reposition login panel dynamically
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                comp.setBounds((width - 400) / 2, (int) (height * 0.5), 400, 300);
            }
        }

    }
    public static void main(String[] args) {
        new LogInForm();
    }
}
