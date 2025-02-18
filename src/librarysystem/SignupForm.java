package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

public class SignupForm extends JFrame {
    private JTextField nameField, emailField, contactField, addressField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton, backButton;
    private JLabel background;
    private ImageIcon[] bgImages;
    private int imageIndex = 0;
    private Timer backgroundTimer;

    public SignupForm() {
        setTitle("Library System - Sign Up");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // Background Images
        bgImages = new ImageIcon[]{
            new ImageIcon(getClass().getResource("/icons/librarybg1.png")),
            new ImageIcon(getClass().getResource("/icons/librarybg2.png")),
            new ImageIcon(getClass().getResource("/icons/librarybg3.png"))
        };

        background = new JLabel(bgImages[0]);
        background.setBounds(0, 0, getWidth(), getHeight());
        getContentPane().add(background);

        backgroundTimer = new Timer(5000, e -> updateBackground());
        backgroundTimer.start();

        // Signup Panel
        JPanel signupPanel = new JPanel();
        signupPanel.setBounds((getWidth() - 400) / 2, (int) (getHeight() * 0.55), 100, 100);
        signupPanel.setBackground(new Color(255, 255, 255, 180));
        signupPanel.setLayout(null);
        signupPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        background.add(signupPanel);

        Font labelFont = new Font("SansSerif", Font.BOLD, 14);
        Color normalGold = new Color(186, 156, 96);
        
        Color hoverGold = new Color(164, 123, 52); // Slightly darker gold
        Color pressedGold = new Color(129, 96, 38); // Even darker gold for pressing effect
        Color textFieldBg = new Color(245, 245, 245); // Light gray background for input fields

        // Name
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 30, 100, 30);
        nameLabel.setFont(labelFont);
        signupPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 30, 200, 30);
        signupPanel.add(nameField);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 70, 100, 30);
        emailLabel.setFont(labelFont);
        signupPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 70, 200, 30);
        signupPanel.add(emailField);

        // Contact
        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setBounds(50, 110, 100, 30);
        contactLabel.setFont(labelFont);
        signupPanel.add(contactLabel);

        contactField = new JTextField();
        contactField.setBounds(150, 110, 200, 30);
        signupPanel.add(contactField);

        // Address
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(50, 150, 100, 30);
        addressLabel.setFont(labelFont);
        signupPanel.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(150, 150, 200, 30);
        signupPanel.add(addressField);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 190, 100, 30);
        passLabel.setFont(labelFont);
        signupPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 190, 200, 30);
        signupPanel.add(passwordField);

        // Role
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(50, 230, 100, 30);
        roleLabel.setFont(labelFont);
        signupPanel.add(roleLabel);

        roleComboBox = new JComboBox<>(new String[]{"Member", "Staff"});
        roleComboBox.setBounds(150, 230, 200, 30);
        signupPanel.add(roleComboBox);

        // Buttons
        registerButton = new JButton("Register");
        registerButton.setBounds(50, 270, 140, 40);
        styleButton(registerButton, normalGold, hoverGold, pressedGold);
        signupPanel.add(registerButton);

        backButton = new JButton("Back");
        backButton.setBounds(210, 270, 140, 40);
        styleButton(backButton, normalGold, hoverGold, pressedGold);
        signupPanel.add(backButton);

        registerButton.addActionListener(e -> registerUser());
        backButton.addActionListener(e -> {
            new LogInForm();
            dispose();
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeBackground();
            }
        });

        setVisible(true);
        resizeBackground();
    }

    private void registerUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String contact = contactField.getText();
        String address = addressField.getText();
        String password = new String(passwordField.getPassword());
        String role = roleComboBox.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            return;
        }

        User.signUp(name, email, password, address, contact, role);
        JOptionPane.showMessageDialog(null, "Registration Successful! Please Login.");
        new LogInForm();
        dispose();
    }

    private void updateBackground() {
        imageIndex = (imageIndex + 1) % bgImages.length;
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
        new SignupForm(); 
    }
}
