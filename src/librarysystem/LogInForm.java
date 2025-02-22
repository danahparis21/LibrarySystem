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
    
     private final Color normalGold = new Color(186, 156, 96); // #ba9c60
    private final Color hoverGold = new Color(164, 123, 52); // Slightly darker gold
    private final Color pressedGold = new Color(129, 96, 38); // Even darker gold
    private final Color textFieldBg = new Color(245, 245, 245); // Light gray for input fields


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
        loginPanel.setBounds(100, 50, 500, 700); // Move more to the left and higher
        loginPanel.setBackground(new Color(255, 255, 255, 200));
        loginPanel.setLayout(null);

        // 🏷️ Huge "Log In" Label
        JLabel loginTitle = new JLabel("Log In");
        loginTitle.setFont(new Font("Arial", Font.BOLD, 50)); // Big bold text
        loginTitle.setForeground(new Color(50, 50, 50)); // Dark gray color
        loginTitle.setBounds(180, 30, 200, 100); // Adjust position inside login panel

        loginPanel.add(loginTitle); // Add label to the panel
        background.add(loginPanel); // Add login panel to background


        // 📌 Label & Input Fields
              
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(60, 250, 150, 40); // Adjusted size
        userLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Increased font size
        loginPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(60, 300, 350, 40);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 18)); // Bigger font for better visibility
        loginPanel.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(60, 350, 150, 40); // Adjusted size
        passLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Increased font size
        loginPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(60, 400, 350, 40);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18)); // Bigger font for better visibility
        loginPanel.add(passwordField);


        // ✅ Login Button
        loginButton = new JButton("Login");
        loginButton.setBounds(80, 500, 140, 40);
        styleButton(loginButton, normalGold, hoverGold, pressedGold);
        loginPanel.add(loginButton);

        // 🚀 Sign Up Button
        signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(250, 500, 140, 40);
         styleButton(signUpButton, normalGold, hoverGold, pressedGold);
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
       private void styleButton(JButton button, Color normalColor, Color hoverColor, Color pressedColor) {
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setFocusPainted(false); // Disable default blue focus border
        button.setBorder(BorderFactory.createLineBorder(new Color(110, 80, 34), 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.setContentAreaFilled(false); // ❗ FIX: Removes default button blue effect
        button.setOpaque(true); // ❗ FIX: Ensures custom colors apply correctly

        // 🔄 Hover & Click Effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(pressedColor); // Custom pressed color
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverColor); // Restore hover color after release
            }
        });
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
                comp.setBounds(100, 50, 500, 700); // Keep it on the left
            }
        }


    }
    public static void main(String[] args) {
        new LogInForm();
    }
}
