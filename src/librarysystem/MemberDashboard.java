package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.JTableHeader;

public class MemberDashboard extends JFrame {

    private JTable bookTable, borrowedTable, reservationTable;
    private JButton borrowButton, renewButton, reserveButton, editProfileButton, notificationsButton, viewDetailsButton, refreshButton, logOutButton, cancelButton;
    private JLabel userProfileLabel, statusLabel;
    private int userID; // Logged-in user ID
    private Connection connection;
    private JLabel profileImage, nameLabel, bioLabel, emailLabel, addressLabel, contactLabel, roleLabel, emailLabelUnder, addressLabelUnder, roleLabelUnder, contactLabelUnder, themeLabel;
    private JTextArea bioField;

    private JButton nextButton, prevButton;
    private ImageIcon[] profileImages;
    private int currentImageIndex = 0;
    
    private JButton nextTheme, prevTheme;
    private Color currentButtonColor;
    private Color currentHoverColor;
    private Color currentPressedColor;
    private Color currentBorderColor;

    private final Map<String, Color[]> themes = new HashMap<>();
    private String[] themeNames = {"Pink", "Sky", "Coffee", "Default"};
      private int currentThemeIndex = 0;

    // Notifications Panel (Dropdown)
    private JPanel notificationsPanel;
    private DefaultListModel<String> notificationListModel;
    private JList<String> notificationList;
    
    private Color borderColor = new Color(0xDEDEDE); // Default border color
    JPanel booksPanel, borrowedBooksPanel, profilePanel, reservationPanel;

    public MemberDashboard(int userID) {
        this.userID = userID;
        setTitle("Member Dashboard");
        setSize(1920, 1080);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connection = Database.connect();

        // Header Image
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/header2.png"));
        JLabel header = new JLabel(icon);
        header.setBounds(0, 0, 800, 80);
        add(header);

        // Status Label
        statusLabel = new JLabel();
        statusLabel.setBounds(20, 10, 600, 30);
        add(statusLabel);

        //=====BOOKS PANEL!    
        booksPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded white background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Smaller rounded corners

                // Draw light gray border (#DEDEDE)
                g2.setColor(borderColor); // Light gray border
                g2.setStroke(new BasicStroke(1)); // Thinner border
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
            }
        };

        // Ensure transparency for custom painting
        booksPanel.setOpaque(false);
        booksPanel.setLayout(null);
        booksPanel.setBounds(20, 80, 850, 300); // Smaller panel size

        // Book List Table
        String[] bookColumns = {"Book ID", "Title", "Author", "Genre"};
        bookTable = new JTable(new DefaultTableModel(bookColumns, 0));
        styleTable(bookTable);
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookScrollPane.setBounds(10, 10, 700, 280); 
        booksPanel.add(bookScrollPane);

        // Buttons (Smaller & Adjusted)
        borrowButton = new JButton("Borrow");
        borrowButton.setBounds(720, 40, 120, 30);
        applyButtonStyles(borrowButton);
        booksPanel.add(borrowButton);

        reserveButton = new JButton("Reserve");
        reserveButton.setBounds(720, 80, 120, 30);
        applyButtonStyles(reserveButton);
        booksPanel.add(reserveButton);

        viewDetailsButton = new JButton("Details");
        viewDetailsButton.setBounds(720, 115, 120, 30);
        applyButtonStyles(viewDetailsButton);
        booksPanel.add(viewDetailsButton);

        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(720, 150, 120, 30);
        applyButtonStyles(refreshButton);
        booksPanel.add(refreshButton);

        // Add books panel to main frame
        add(booksPanel);

        //====BORROWED BOOKS PANEL:
        borrowedBooksPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded white background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Smaller rounded corners

                // Draw light gray border (#DEDEDE)
                g2.setColor(borderColor); // Light gray border
                g2.setStroke(new BasicStroke(1)); // Thinner border
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
            }
        };

        // Ensure transparency for custom painting
        borrowedBooksPanel.setOpaque(false);
        borrowedBooksPanel.setLayout(null);
        borrowedBooksPanel.setBounds(20, 390, 570, 370); 

        // Borrowed Books Table
        String[] borrowedColumns = {"Borrow ID", "Book ID", "Title", "Due Date", "Status"};
        borrowedTable = new JTable(new DefaultTableModel(borrowedColumns, 0));
        styleTable(borrowedTable);
        JScrollPane borrowedScrollPane = new JScrollPane(borrowedTable);
        borrowedScrollPane.setBounds(10, 10, 550, 280);
        borrowedBooksPanel.add(borrowedScrollPane);

        renewButton = new JButton("Renew Book");
        renewButton.setBounds(350, 300, 200, 50);
        applyButtonStyles(renewButton);
        borrowedBooksPanel.add(renewButton);
        add(borrowedBooksPanel);

        //====RESERVATION PANEL
       reservationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded white background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Smaller rounded corners

                // Draw light gray border (#DEDEDE)
                g2.setColor(borderColor); // Light gray border
                g2.setStroke(new BasicStroke(1)); // Thinner border
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
            }
        };

        // Ensure transparency for custom painting
        reservationPanel.setOpaque(false);
        reservationPanel.setLayout(null);
        reservationPanel.setBounds(600, 390, 500, 370); // Smaller panel size

        // Reservation Table
        String[] reservationColumns = {"Reservation ID", "Book ID", "Reservation Date", "Status"};
        reservationTable = new JTable(new DefaultTableModel(reservationColumns, 0));
        styleTable(reservationTable);
        JScrollPane reservationScrollPane = new JScrollPane(reservationTable);
        reservationScrollPane.setBounds(10, 10, 480, 280);
        reservationPanel.add(reservationScrollPane);

        cancelButton = new JButton("Cancel a Reservation");
        cancelButton.setBounds(290, 300, 200, 50);
        applyButtonStyles(cancelButton);
        reservationPanel.add(cancelButton);

        add(reservationPanel);

        // User Profile Section
        // Profile Panel
           
        profilePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded white background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Smaller rounded corners

                // Draw light gray border (#DEDEDE)
                g2.setColor(new Color(0xDEDEDE)); // Light gray border
                g2.setStroke(new BasicStroke(2)); // Thinner border
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
            }
        };

        // Ensure transparency for custom painting
        profilePanel.setOpaque(false);
        
        profilePanel.setLayout(null);
        profilePanel.setBounds(1130, 5, 500, 780);

        // Load predefined profile images
        profileImages = new ImageIcon[]{
            new ImageIcon(getClass().getResource("/icons/profile1.jpg")),
            //new ImageIcon(getClass().getResource("/icons/profile2.jpg")),
            new ImageIcon(getClass().getResource("/icons/profile3.jpg")),
            new ImageIcon(getClass().getResource("/icons/profile4.jpg")),
            new ImageIcon(getClass().getResource("/icons/profile5.jpg")),
            //new ImageIcon(getClass().getResource("/icons/profile6.jpg")),
            new ImageIcon(getClass().getResource("/icons/profile7.jpg")),
            new ImageIcon(getClass().getResource("/icons/profile8.jpg")),
            new ImageIcon(getClass().getResource("/icons/profile9.jpg"))

        };

        // Profile Image Label
        profileImage = new JLabel();
        profileImage.setBounds(90, 8, 210, 210);

        updateProfileImage(); // Set the initial image

        profilePanel.revalidate();
        profilePanel.repaint();

        // Next & Previous Buttons
        ImageIcon rightIcon = new ImageIcon(getClass().getResource("/icons/right_arrow.png"));
        ImageIcon leftIcon = new ImageIcon(getClass().getResource("/icons/left_arrow.png"));

        nextButton = new JButton(rightIcon);
        nextButton.setBounds(260, 220, 50, 50); // Positioned correctly
        nextButton.setContentAreaFilled(false);
        nextButton.setBorderPainted(false);
        nextButton.addActionListener(e -> nextImage());

        prevButton = new JButton(leftIcon);
        prevButton.setBounds(90, 220, 50, 50); // Adjusted size for a better look
        prevButton.setContentAreaFilled(false); // Makes button background transparent
        prevButton.setBorderPainted(false); // Removes button border
        prevButton.addActionListener(e -> previousImage());

        profilePanel.add(profileImage);
        profilePanel.add(profileImage);
        profilePanel.add(nextButton);
        profilePanel.add(prevButton);

        // Create the name label
        nameLabel = new JLabel("Welcome, ");
        nameLabel.setFont(new Font("Serif", Font.ITALIC, 24)); // Bigger and italicized
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text
        nameLabel.setBounds(20, 260, 350, 40); // Adjust bounds (X, Y, Width, Height)
        profilePanel.add(nameLabel);

        // Create and position the bio label
        bioLabel = new JLabel("Bio:");
        bioLabel.setBounds(10, 390, 300, 50);

        // Create a larger, scrollable bio field
        bioField = new JTextArea(3, 100); // 3 rows, 20 columns
        bioField.setWrapStyleWord(true);
        bioField.setLineWrap(true);
        bioField.setFont(new Font("Arial", Font.PLAIN, 14)); // Make it look cleaner
        bioField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Add a border
        bioField.setForeground(new Color(64, 64, 64)); // Slightly darker text color

        // Wrap bioField in a JScrollPane for scrolling (helps if the bio is long!)
        JScrollPane bioScroll = new JScrollPane(bioField);
        bioScroll.setBounds(30, 300, 340, 80); // Adjusted for better spacing

        bioField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { // Detect Enter key
                    e.consume(); // Prevent new line in JTextArea
                    saveBioToDatabase();
                }
            }
        });

        // For the main email label – similar to nameLabel but a bit smaller and italic
        emailLabel = createStyledLabel("", 18, Font.ITALIC, Color.BLACK, 55, 390, 255, 30);
        profilePanel.add(emailLabel);

        // For the email "under" label – smaller, plain, and gray
        emailLabelUnder = createStyledLabel("Email", 12, Font.PLAIN, Color.GRAY, 50, 410, 250, 25);
        profilePanel.add(emailLabelUnder);

        addressLabel = createStyledLabel("", 18, Font.ITALIC, Color.BLACK, 55, 430, 255, 30);
        profilePanel.add(addressLabel);

        addressLabelUnder = createStyledLabel("Address", 12, Font.PLAIN, Color.GRAY, 50, 450, 250, 25);
        profilePanel.add(addressLabelUnder);

        contactLabel = createStyledLabel("", 18, Font.ITALIC, Color.BLACK, 55, 470, 255, 30);
        profilePanel.add(contactLabel);

        contactLabelUnder = createStyledLabel("Contact", 12, Font.PLAIN, Color.GRAY, 50, 490, 250, 25);
        profilePanel.add(contactLabelUnder);

        roleLabel = createStyledLabel("", 18, Font.ITALIC, Color.BLACK, 50, 510, 255, 30);
        profilePanel.add(roleLabel);

        roleLabelUnder = createStyledLabel("Role", 12, Font.PLAIN, Color.GRAY, 50, 530, 250, 25);
        profilePanel.add(roleLabelUnder);
        
        //=======
        
        // Initialize themes
        themes.put("Pink", new Color[]{new Color(0xffe6ed), new Color(0xf4c0d0), new Color(0xf4c0d0), new Color(0xf4b2c4)});
        themes.put("Sky", new Color[]{new Color(0xd8ddff), new Color(0xc0c7ff), new Color(0xa8b1ff), new Color(0xd8ddff)});
        themes.put("Coffee", new Color[]{new Color(0xe7dfd2), new Color(0xad8b76), new Color(0xe7dfd2), new Color(0xad8b76)});
        themes.put("Default", new Color[]{new Color(0xDEDEDE), new Color(0xCCCCCC), new Color(0xAAAAAA), new Color(0xDEDEDE)});
        

        

        // Theme Switch Buttons
        nextTheme = new JButton(">");
        nextTheme.setBounds(240, 560, 50, 50);
        nextTheme.addActionListener(e -> nextColorTheme());
        applyButtonStyles(nextTheme);

        prevTheme = new JButton("<");
        prevTheme.setBounds(80, 560, 50, 50);
        prevTheme.addActionListener(e -> previousColorTheme());
        applyButtonStyles(prevTheme);
        
        // Theme Label (In Between Buttons)
        themeLabel = new JLabel("Pink"); 
        themeLabel.setBounds(130, 560, 100, 50);
        themeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        themeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        profilePanel.add(themeLabel); 

        profilePanel.add(nextTheme);
        profilePanel.add(prevTheme);

        editProfileButton = new JButton("Edit Profile");
        editProfileButton.setBounds(110, 620, 150, 50);

        editProfileButton.addActionListener(e -> new editProfile(userID).setVisible(true));
        applyButtonStyles(editProfileButton);
        
        profilePanel.add(editProfileButton);
        profilePanel.add(bioScroll);

        logOutButton = new JButton("Log Out");
        logOutButton.setBounds(110, 700, 150, 50);
        applyButtonStyles(logOutButton);
        profilePanel.add(logOutButton);
        

        // Add profilePanel to the main container
        add(profilePanel);

        //======
        // Notifications Button
        notificationsButton = new JButton("Notifications");
        notificationsButton.setBounds(970, 10, 150, 50);
        applyButtonStyles(notificationsButton);
        add(notificationsButton);

       // Notifications Panel
        notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BorderLayout());
        notificationsPanel.setBounds(875, 70, 250, 320); // Increased size for better visibility
        notificationsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        notificationsPanel.setBackground(Color.WHITE);
        notificationsPanel.setVisible(false); // Hidden by default

        // Panel Title
        JLabel notificationTitle = new JLabel("Notifications");
        notificationTitle.setFont(new Font("Arial", Font.BOLD, 18));
        notificationTitle.setHorizontalAlignment(SwingConstants.CENTER);
        notificationTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        notificationsPanel.add(notificationTitle, BorderLayout.NORTH);

        // Scrollable List of Notifications
        notificationListModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationListModel);
        notificationList.setFont(new Font("Arial", Font.PLAIN, 14)); // Bigger & better font
        notificationList.setFixedCellHeight(35); // Increase row height for readability
        notificationList.setSelectionBackground(new Color(173, 216, 230)); // Light blue highlight

        // ScrollPane for Notifications
        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Added padding
        notificationsPanel.add(scrollPane, BorderLayout.CENTER);

        add(notificationsPanel);

        // Show/hide notifications on button click
        notificationsButton.addActionListener(e -> {
            notificationsPanel.setVisible(!notificationsPanel.isVisible());
            loadNotifications();
        });

        // Handling Click on Notifications
        notificationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedNotification = notificationList.getSelectedValue();
                if (selectedNotification != null) {
                    if (selectedNotification.contains("overdue")) {
                        JOptionPane.showMessageDialog(this, 
                            "⚠️ Overdue Notice!\nPlease go to the Library to return the book and pay fines.", 
                            "Overdue Alert", JOptionPane.WARNING_MESSAGE);
                    }
                    if (selectedNotification.contains("reservation")) {
                        JOptionPane.showMessageDialog(this, 
                            "✅ Your Book is Ready!\nProceed to the Library to Pick up your Book!\nThanks for your patience.", 
                            "Reservation Ready", JOptionPane.INFORMATION_MESSAGE);
                    }
                    notificationsPanel.setVisible(false); // Hide panel after clicking
                }
            }
        });


        // Load Data
        loadBooks();
        loadBorrowedBooks();
        loadReservations();
        loadUserProfile();
        checkOverdueBooks();
        checkReservations();
        loadUserProfile();

        // Button Listeners
        cancelButton.addActionListener(e -> cancelBook());
        viewDetailsButton.addActionListener(e -> viewBookDetails());
        borrowButton.addActionListener(e -> borrowBook());
        renewButton.addActionListener(e -> renewBook());
        reserveButton.addActionListener(e -> reserveBook());
        refreshButton.addActionListener(e -> {
            loadBooks();
            loadBorrowedBooks();
            loadReservations();
            loadUserProfile();
            checkOverdueBooks();
            checkReservations();
            loadUserProfile();
            statusLabel.setText("Data refreshed.");
        });

        logOutButton.addActionListener(e -> {
            new LogInForm().setVisible(true);
            dispose();
        });

        // Apply the first theme
        applyTheme(themeNames[currentThemeIndex]);
           
        setVisible(true);
    }
    
         private void applyTheme(String themeName) {
            Color[] colors = themes.get(themeName);
            if (colors == null) return;

            currentButtonColor = colors[0];
            currentHoverColor = colors[1];
            currentPressedColor = colors[2];
            Color newBorderColor = colors[3]; // Get the theme border color

            // Update border color for all panels
            borderColor = newBorderColor; 
            booksPanel.repaint();
            borrowedBooksPanel.repaint();
            
            reservationPanel.repaint();

            // Apply to buttons
            applyButtonStyles(nextTheme);
            applyButtonStyles(prevTheme);
        }


         
    private void applyButtonStyles(JButton button) {
            button.setBackground(currentButtonColor);
            button.setFont(new Font("SansSerif", Font.BOLD, 14));
            
            //button.setBorder(BorderFactory.createLineBorder(currentBorderColor, 2));
            button.setFocusPainted(false);
            button.setOpaque(true);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(currentHoverColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(currentButtonColor);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    button.setBackground(currentPressedColor);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    button.setBackground(currentHoverColor);
                }
            });
        }
    private JButton createButton(String text, int x, int y, JPanel panel) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBounds(x, y, 200, 50);

        // Light theme colors
        Color defaultBg = Color.WHITE;
        Color defaultFg = new Color(0x393939);
        Color hoverBg = new Color(0xE0E0E0);
        Color hoverFg = Color.BLACK;

        // Apply default styling
        button.setBackground(defaultBg);
        button.setForeground(defaultFg);
        button.setBorder(BorderFactory.createLineBorder(new Color(0xD3D3D3), 1));
        button.setFocusPainted(false);
        button.setOpaque(true);

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverBg);
                button.setForeground(hoverFg);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(defaultBg);
                button.setForeground(defaultFg);
            }
        });

        panel.add(button);
        return button;
    }

        private void nextColorTheme() {
        currentThemeIndex = (currentThemeIndex + 1) % themeNames.length;
            applyTheme(themeNames[currentThemeIndex]);
            updateThemeLabel();
        }

        private void previousColorTheme() {
            currentThemeIndex = (currentThemeIndex - 1 + themeNames.length) % themeNames.length;
            applyTheme(themeNames[currentThemeIndex]);
            updateThemeLabel();
        }
        
        private void updateThemeLabel() {
            String[] themeNames = {"Pink", "Sky", "Coffee", "Default"};
            themeLabel.setText(themeNames[currentThemeIndex]);
}

    private void loadUserProfile() {
        try {
            String query = "SELECT name, bio, email, address, contact, role FROM Users WHERE userID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameLabel.setText("Welcome, " + rs.getString("name") + "!");

                // ✅ Fix bio null handling & add some formatting
                String bioText = rs.getString("bio");
                bioField.setText(bioText != null && !bioText.trim().isEmpty() ? bioText : "No bio set.");

                emailLabel.setText("" + rs.getString("email"));
                addressLabel.setText("" + rs.getString("address"));
                contactLabel.setText("" + rs.getString("contact"));
                roleLabel.setText("" + rs.getString("role"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewBookDetails() {
        int selectedRow = bookTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to view details.", "No Book Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookID = (int) bookTable.getValueAt(selectedRow, 0); // Assuming Book ID is the first column

        try {
            String query = "SELECT * FROM Books WHERE bookID = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, bookID);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Fetch book details
                String title = rs.getString("title");
                String author = rs.getString("author");
                String isbn = rs.getString("ISBN");
                String genre = rs.getString("genre");
                String publisher = rs.getString("publisher");
                int publicationYear = rs.getInt("publicationYear");
                int quantity = rs.getInt("quantity");
                String location = rs.getString("location");

                // Display details in a pop-up dialog
                JDialog detailsDialog = new JDialog(this, "Book Details", true);
                detailsDialog.setSize(400, 300);
                detailsDialog.setLayout(new GridLayout(9, 1));

                detailsDialog.add(new JLabel("Title: " + title));
                detailsDialog.add(new JLabel("Author: " + author));
                detailsDialog.add(new JLabel("ISBN: " + isbn));
                detailsDialog.add(new JLabel("Genre: " + genre));
                detailsDialog.add(new JLabel("Publisher: " + publisher));
                detailsDialog.add(new JLabel("Publication Year: " + publicationYear));
                detailsDialog.add(new JLabel("Quantity: " + quantity));
                detailsDialog.add(new JLabel("Location: " + location));

                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(e -> detailsDialog.dispose());
                detailsDialog.add(closeButton);

                detailsDialog.setLocationRelativeTo(this);
                detailsDialog.setVisible(true);
            }

            rs.close();
            pst.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error retrieving book details: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to update profile image
    private void updateProfileImage() {
        if (profileImages[currentImageIndex].getIconWidth() > 0) {
            Image image = profileImages[currentImageIndex].getImage().getScaledInstance(210, 210, Image.SCALE_SMOOTH);
            profileImage.setIcon(new ImageIcon(image));
        }
    }

    // Method to switch to the next image
    private void nextImage() {
        currentImageIndex = (currentImageIndex + 1) % profileImages.length;
        updateProfileImage();
    }

    // Method to switch to the previous image
    private void previousImage() {
        currentImageIndex = (currentImageIndex - 1 + profileImages.length) % profileImages.length;
        updateProfileImage();
    }

    private void styleTable(JTable table) {
        // Table Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setBackground(new Color(0xF0F0F0)); // Light Gray Header
        header.setForeground(new Color(0x393939));
        header.setOpaque(true);

        // Table Body Styling
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(0x393939));
        table.setGridColor(new Color(0xE0E0E0)); // Lighter Grid Color
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Selection Styling
        table.setSelectionBackground(new Color(0xE0E0E0)); // Soft Gray Selection
        table.setSelectionForeground(new Color(0x393939));

        // Borderless Look
        table.setBorder(BorderFactory.createEmptyBorder());
    }

    private JLabel createStyledLabel(String text, int fontSize, int fontStyle, Color color, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", fontStyle, fontSize));
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(x, y, width, height);
        return label;
    }

    private JTextField createTextField(String text, int x, int y, JPanel panel) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("SansSerif", Font.PLAIN, 18)); // Match overall style
        textField.setBounds(x, y, 120, 35);
        textField.setBorder(BorderFactory.createLineBorder(new Color(0xD3D3D3), 1)); // Subtle border
        panel.add(textField);
        return textField;
    }

    

    private void saveBioToDatabase() {
        try {
            String newBio = bioField.getText().trim(); // Get text & remove spaces
            String query = "UPDATE Users SET bio = ? WHERE userID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, newBio);
            stmt.setInt(2, userID);
            int rowsUpdated = stmt.executeUpdate();
            stmt.close();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "Bio updated successfully! 💾✨");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update bio! ❌");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNotifications() {
        notificationListModel.clear();
        try {
            String query = "SELECT message FROM Notifications WHERE userID = ? ORDER BY createdAt DESC";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                notificationListModel.addElement(rs.getString("message"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkReservations() {
        try {
            String query = "SELECT reservationID, bookID FROM Reservations "
                    + "WHERE userID = ? AND status = 'Notified'";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int bookID = rs.getInt("bookID");
                String message = "Your reserved book (ID: " + bookID + ") is now available for pickup.";

                // Check if notification already exists
                PreparedStatement checkNotif = connection.prepareStatement(
                        "SELECT COUNT(*) FROM Notifications WHERE userID = ? AND message = ?"
                );
                checkNotif.setInt(1, userID);
                checkNotif.setString(2, message);
                ResultSet notifRs = checkNotif.executeQuery();

                if (notifRs.next() && notifRs.getInt(1) == 0) { // If no existing notification
                    PreparedStatement insertNotif = connection.prepareStatement(
                            "INSERT INTO Notifications (userID, message) VALUES (?, ?)");
                    insertNotif.setInt(1, userID);
                    insertNotif.setString(2, message);
                    insertNotif.executeUpdate();
                    insertNotif.close();
                }

                notifRs.close();
                checkNotif.close();
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkOverdueBooks() {
        try {
            String query = "SELECT borrowID, bookID, dueDate FROM BorrowedBooks WHERE userID = ? AND dueDate < CURDATE()";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int bookID = rs.getInt("bookID");
                String dueDate = rs.getString("dueDate");
                String message = "Your borrowed book (ID: " + bookID + ") is overdue since " + dueDate + ".";

                // Check if notification already exists
                PreparedStatement checkNotif = connection.prepareStatement(
                        "SELECT COUNT(*) FROM Notifications WHERE userID = ? AND message = ?"
                );
                checkNotif.setInt(1, userID);
                checkNotif.setString(2, message);
                ResultSet notifRs = checkNotif.executeQuery();

                if (notifRs.next() && notifRs.getInt(1) == 0) { // If no existing notification
                    PreparedStatement insertNotif = connection.prepareStatement(
                            "INSERT INTO Notifications (userID, message) VALUES (?, ?)");
                    insertNotif.setInt(1, userID);
                    insertNotif.setString(2, message);
                    insertNotif.executeUpdate();
                    insertNotif.close();
                }

                notifRs.close();
                checkNotif.close();
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reserveBook() {
        try {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                statusLabel.setText("Please select a book to reserve.");
                return;
            }

            int bookID = (int) bookTable.getValueAt(selectedRow, 0);
            String reservationDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

            // Check if book is available (quantity > 0)
            PreparedStatement checkAvailability = connection.prepareStatement(
                    "SELECT quantity FROM Books WHERE bookID = ?"
            );
            checkAvailability.setInt(1, bookID);
            ResultSet rs = checkAvailability.executeQuery();

            if (rs.next() && rs.getInt("quantity") > 0) {
                statusLabel.setText("Book is available. No need for reservation.");
                return;
            }

            // Count existing reservations for this book to determine queue position
            PreparedStatement countReservations = connection.prepareStatement(
                    "SELECT COUNT(*) AS queuePosition FROM Reservations WHERE bookID = ? AND status = 'Pending'"
            );
            countReservations.setInt(1, bookID);
            ResultSet queueResult = countReservations.executeQuery();

            int queueNumber = 1; // Default if no reservations exist
            if (queueResult.next()) {
                queueNumber = queueResult.getInt("queuePosition") + 1; // Next queue number
            }

            // Insert new reservation
            PreparedStatement reserveStmt = connection.prepareStatement(
                    "INSERT INTO Reservations (userID, bookID, reservationDate, status) VALUES (?, ?, ?, 'Pending')"
            );
            reserveStmt.setInt(1, userID);
            reserveStmt.setInt(2, bookID);
            reserveStmt.setString(3, reservationDate);
            reserveStmt.executeUpdate();

            statusLabel.setText("Book reserved successfully! You are queue number " + queueNumber);
            loadReservations(); // Refresh reservations table
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error reserving book.");
        }
    }

    private void borrowBook() {
        try {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                statusLabel.setText("Please select a book to borrow.");
                return;
            }

            int bookID = (int) bookTable.getValueAt(selectedRow, 0);
            String borrowDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            String dueDate = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000));

            PreparedStatement checkAvailability = connection.prepareStatement("SELECT quantity FROM Books WHERE bookID = ?");
            checkAvailability.setInt(1, bookID);
            ResultSet rs = checkAvailability.executeQuery();

            if (rs.next() && rs.getInt("quantity") > 0) {
                PreparedStatement borrowStmt = connection.prepareStatement("INSERT INTO BorrowedBooks (userID, bookID, borrowDate, dueDate) VALUES (?, ?, ?, ?)");
                borrowStmt.setInt(1, userID);
                borrowStmt.setInt(2, bookID);
                borrowStmt.setString(3, borrowDate);
                borrowStmt.setString(4, dueDate);
                borrowStmt.executeUpdate();

                PreparedStatement updateStock = connection.prepareStatement("UPDATE Books SET quantity = quantity - 1 WHERE bookID = ?");
                updateStock.setInt(1, bookID);
                updateStock.executeUpdate();

                statusLabel.setText("Book borrowed successfully! Due date: " + dueDate);
                loadBooks();
                loadBorrowedBooks();
            } else {
                statusLabel.setText("Book not available.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error borrowing book.");
        }
    }

    private void renewBook() {
        try {
            int selectedRow = borrowedTable.getSelectedRow();
            if (selectedRow == -1) {
                statusLabel.setText("Please select a borrowed book to renew.");
                return;
            }

            int borrowID = (int) borrowedTable.getValueAt(selectedRow, 0); // Column 0 = Borrow ID

            System.out.println("DEBUG: Renewing borrowID = " + borrowID);

            // STEP 1: Get the current due date
            PreparedStatement checkStmt = connection.prepareStatement(
                    "SELECT dueDate, returnDate FROM BorrowedBooks WHERE borrowID = ?"
            );
            checkStmt.setInt(1, borrowID);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                statusLabel.setText("Renewal failed. This borrow record does not exist.");
                return;
            }

            if (rs.getString("returnDate") != null) {
                statusLabel.setText("Renewal failed. This book has already been returned.");
                return;
            }

            // Get the current due date from the database
            String currentDueDateStr = rs.getString("dueDate");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date currentDueDate = sdf.parse(currentDueDateStr);

            // Add 1 day (for testing) instead of 14
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDueDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Change 1 to 14 for actual renewal policy

            String newDueDate = sdf.format(calendar.getTime());

            // STEP 2: Update the due date in the database
            PreparedStatement renewStmt = connection.prepareStatement(
                    "UPDATE BorrowedBooks SET dueDate = ? WHERE borrowID = ? AND returnDate IS NULL"
            );
            renewStmt.setString(1, newDueDate);
            renewStmt.setInt(2, borrowID);

            int updated = renewStmt.executeUpdate();

            if (updated > 0) {
                statusLabel.setText("Book renewed successfully! New due date: " + newDueDate);
                loadBorrowedBooks(); // Refresh table
            } else {
                statusLabel.setText("Renewal failed. Please check the borrow details.");
            }
        } catch (SQLException | java.text.ParseException e) {
            e.printStackTrace();
            statusLabel.setText("Error renewing book.");
        }
    }

    private void loadBooks() {
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM Books")) {
            DefaultTableModel model = (DefaultTableModel) bookTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("bookID"), rs.getString("title"), rs.getString("author"), rs.getString("genre"), rs.getInt("quantity"), rs.getString("location")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBorrowedBooks() {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT b.borrowID, b.bookID, bo.title, b.dueDate FROM BorrowedBooks b JOIN Books bo ON b.bookID = bo.bookID WHERE b.userID = ? and b.status != 'Returned'")) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) borrowedTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("borrowID"), rs.getInt("bookID"), rs.getString("title"), rs.getDate("dueDate"), "Borrowed"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadReservations() {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Reservations WHERE userID = ?")) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) reservationTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("reservationID"), rs.getInt("bookID"), rs.getDate("reservationDate"), rs.getString("status")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cancelBook() {
        try {
            int selectedRow = reservationTable.getSelectedRow();
            if (selectedRow == -1) {
                statusLabel.setText("Please select a reservation to cancel.");
                return;
            }

            int reservationID = (int) reservationTable.getValueAt(selectedRow, 0); // Column 0 = Reservation ID
            String currentStatus = (String) reservationTable.getValueAt(selectedRow, 3); // Column 3 = Status

            if (!"Pending".equals(currentStatus)) {
                statusLabel.setText("Only pending reservations can be canceled.");
                return;
            }

            // Update reservation status to "Cancelled"
            PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE Reservations SET status = 'Cancelled' WHERE reservationID = ?"
            );
            updateStmt.setInt(1, reservationID);
            int affectedRows = updateStmt.executeUpdate();

            if (affectedRows > 0) {
                statusLabel.setText("Reservation canceled successfully.");
                loadReservations(); // Refresh the reservations table
            } else {
                statusLabel.setText("Failed to cancel reservation.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error canceling reservation.");
        }
    }

    public static void main(String[] args) {
        int userID = 1;
        new MemberDashboard(userID).setVisible(true);
    }

}
