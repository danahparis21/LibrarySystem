package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MemberDashboard extends JFrame {
    private JTable bookTable, borrowedTable, reservationTable;
    private JButton borrowButton, renewButton, reserveButton, editProfileButton, notificationsButton, viewDetailsButton, refreshButton, logOutButton, cancelButton;
    private JLabel userProfileLabel, statusLabel;
    private int userID; // Logged-in user ID
    private Connection connection;
    private JLabel profileImage, nameLabel, bioLabel, emailLabel, addressLabel, contactLabel, roleLabel;
    private JTextArea bioField;
    
    
    // Notifications Panel (Dropdown)
        private JPanel notificationsPanel;
        private DefaultListModel<String> notificationListModel;
        private JList<String> notificationList;

    public MemberDashboard(int userID) {
        this.userID = userID;
        setTitle("Member Dashboard");
        setSize(1920, 1080);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connection = Database.connect();
        
        // Status Label
        statusLabel = new JLabel();
        statusLabel.setBounds(20, 10, 600, 30);
        add(statusLabel);

        // Book List Table
        String[] bookColumns = {"Book ID", "Title", "Author", "Genre", "Quantity", "Location"};
        bookTable = new JTable(new DefaultTableModel(bookColumns, 0));
        JScrollPane bookScrollPane = new JScrollPane(bookTable);
        bookScrollPane.setBounds(20, 50, 600, 300);
        add(bookScrollPane);
        
        borrowButton = new JButton("Borrow Book");
        borrowButton.setBounds(630, 50, 150, 30);
        add(borrowButton);
        
        reserveButton = new JButton("Reserve Book");
        reserveButton.setBounds(630, 90, 150, 30);
        add(reserveButton);
        
        viewDetailsButton = new JButton("View Book Details");
        viewDetailsButton.setBounds(630, 130, 150, 30);
        add(viewDetailsButton);
        
        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(630, 170, 150, 30);
        add(refreshButton);
        
        // Borrowed Books Table
        String[] borrowedColumns = {"Borrow ID", "Book ID", "Title", "Due Date", "Status"};
        borrowedTable = new JTable(new DefaultTableModel(borrowedColumns, 0));
        JScrollPane borrowedScrollPane = new JScrollPane(borrowedTable);
        borrowedScrollPane.setBounds(20, 370, 600, 200);
        add(borrowedScrollPane);
        
        renewButton = new JButton("Renew Book");
        renewButton.setBounds(630, 370, 150, 30);
        add(renewButton);
        
        // Reservation Table
        String[] reservationColumns = {"Reservation ID", "Book ID", "Reservation Date", "Status"};
        reservationTable = new JTable(new DefaultTableModel(reservationColumns, 0));
        JScrollPane reservationScrollPane = new JScrollPane(reservationTable);
        reservationScrollPane.setBounds(20, 580, 600, 200);
        add(reservationScrollPane);
        
        cancelButton = new JButton("Cancel a Reservation");
        cancelButton.setBounds(630, 470, 150, 30);
        add(cancelButton);
        
        // User Profile Section
        // Profile Panel
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(null); // Set null layout
        profilePanel.setBorder(BorderFactory.createTitledBorder("Profile"));
        profilePanel.setBounds(1000, 10, 250, 400); // Adjust position and size manually

        // Default Profile Image
        profileImage = new JLabel(new ImageIcon("default_profile.jpg"));
        profileImage.setBounds(75, 20, 100, 100); // Adjust size and position manually

        nameLabel = new JLabel("Name: ");
        nameLabel.setBounds(20, 130, 200, 20);

        // Create and position the bio label
        bioLabel = new JLabel("Bio:");
        bioLabel.setBounds(20, 160, 200, 20);

        // Create a larger, scrollable bio field
        bioField = new JTextArea(3, 20); // 3 rows, 20 columns
        bioField.setWrapStyleWord(true);
        bioField.setLineWrap(true);
        bioField.setFont(new Font("Arial", Font.PLAIN, 14)); // Make it look cleaner
        bioField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Add a border
        bioField.setForeground(new Color(64, 64, 64)); // Slightly darker text color

        // Wrap bioField in a JScrollPane for scrolling (helps if the bio is long!)
        JScrollPane bioScroll = new JScrollPane(bioField);
        bioScroll.setBounds(40, 160, 200, 60); // Adjusted for better spacing
        
        bioField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { // Detect Enter key
                    e.consume(); // Prevent new line in JTextArea
                    saveBioToDatabase();
                }
            }
        });


        emailLabel = new JLabel("Email: ");
        emailLabel.setBounds(20, 210, 200, 20);

        addressLabel = new JLabel("Address:");
        addressLabel.setBounds(20, 230, 200, 20);

        contactLabel = new JLabel("Contact: ");
        contactLabel.setBounds(20, 260, 200, 20);

        roleLabel = new JLabel("Role: Member");
        roleLabel.setBounds(20, 290, 200, 20);

        editProfileButton = new JButton("Edit Profile");
        editProfileButton.setBounds(75, 320, 100, 30);

        editProfileButton.addActionListener(e -> new editProfile(userID).setVisible(true));

        // Add components to profilePanel
        profilePanel.add(profileImage);
        profilePanel.add(nameLabel);
        profilePanel.add(bioLabel);
        profilePanel.add(emailLabel);
        profilePanel.add(addressLabel);
        profilePanel.add(contactLabel);
        profilePanel.add(roleLabel);
        profilePanel.add(editProfileButton);
        profilePanel.add(bioScroll);


        // Add profilePanel to the main container
        add(profilePanel);

        
        
        
        
        
        //======
        
        
        
        logOutButton = new JButton("Log Out");
        logOutButton.setBounds(1200, 500, 150, 30);
        add(logOutButton);
        
        // Notifications Button
        notificationsButton = new JButton("Notifications");
        notificationsButton.setBounds(800, 150, 150, 30);
        add(notificationsButton);
        
          notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BorderLayout());
        notificationsPanel.setBounds(800, 180, 250, 300); // Position below the button
        notificationsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        notificationsPanel.setBackground(Color.WHITE);
        notificationsPanel.setVisible(false); // Hidden by default

        // Scrollable list of notifications
        notificationListModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationListModel);
        JScrollPane scrollPane = new JScrollPane(notificationList);
        notificationsPanel.add(scrollPane, BorderLayout.CENTER);

        add(notificationsPanel);
        // Show/hide notifications on button click
        notificationsButton.addActionListener(e -> {
            notificationsPanel.setVisible(!notificationsPanel.isVisible());
            loadNotifications();
            
        });
        notificationList.addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        String selectedNotification = notificationList.getSelectedValue();
        if (selectedNotification != null) {
            if (selectedNotification.contains("overdue")) {
                JOptionPane.showMessageDialog(this, "Please go to the Library to return the book and pay fines.");
            }
            if (selectedNotification.contains("reservation")) {
                JOptionPane.showMessageDialog(this, "Proceed to Library to Pick up your Book!\n Thanks for your Patience");
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

        setVisible(true);
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

   
        private void loadUserProfile() {
         try {
             String query = "SELECT name, bio, email, address, contact, role FROM Users WHERE userID = ?";
             PreparedStatement stmt = connection.prepareStatement(query);
             stmt.setInt(1, userID);
             ResultSet rs = stmt.executeQuery();

             if (rs.next()) {
                 nameLabel.setText("Name: " + rs.getString("name"));

                 // ✅ Fix bio null handling & add some formatting
                 String bioText = rs.getString("bio");
                 bioField.setText(bioText != null && !bioText.trim().isEmpty() ? bioText : "No bio set 📝");

                 emailLabel.setText("Email: " + rs.getString("email"));
                 addressLabel.setText("Address: " + rs.getString("address"));
                 contactLabel.setText("Contact: " + rs.getString("contact"));
                 roleLabel.setText("Role: " + rs.getString("role"));
             }

             rs.close();
             stmt.close();
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
        String query = "SELECT reservationID, bookID FROM Reservations " +
                       "WHERE userID = ? AND status = 'Notified'";

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
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Books")) {
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
        try ( PreparedStatement stmt = connection.prepareStatement("SELECT b.borrowID, b.bookID, bo.title, b.dueDate FROM BorrowedBooks b JOIN Books bo ON b.bookID = bo.bookID WHERE b.userID = ? and b.status != 'Returned'")) {
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

    
    
}
