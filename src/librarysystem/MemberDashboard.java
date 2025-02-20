package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MemberDashboard extends JFrame {
    private JTable bookTable, borrowedTable, reservationTable;
    private JButton borrowButton, renewButton, reserveButton, editProfileButton, notificationsButton, viewDetailsButton, refreshButton, logOutButton, cancelButton;
    private JLabel userProfileLabel, statusLabel;
    private int userID; // Logged-in user ID
    private Connection connection;
    
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
        userProfileLabel = new JLabel("User Profile");
        userProfileLabel.setBounds(900, 50, 1000, 30);
        add(userProfileLabel);
        
        editProfileButton = new JButton("Edit Profile");
        editProfileButton.setBounds(1200, 100, 150, 30);
        add(editProfileButton);
        
        logOutButton = new JButton("Log Out");
        logOutButton.setBounds(1200, 500, 150, 30);
        add(logOutButton);
        
        // Notifications Button
        notificationsButton = new JButton("Notifications");
        notificationsButton.setBounds(1200, 150, 150, 30);
        add(notificationsButton);
        
        // Load Data
        loadBooks();
        loadBorrowedBooks();
        loadReservations();
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
            statusLabel.setText("Data refreshed.");
        });
        
        logOutButton.addActionListener(e -> {
            new LogInForm().setVisible(true);
            dispose();
                });

        setVisible(true);
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

            // Insert notification
            PreparedStatement insertNotif = connection.prepareStatement(
                "INSERT INTO Notifications (userID, message) VALUES (?, ?)");
            insertNotif.setInt(1, userID);
            insertNotif.setString(2, message);
            insertNotif.executeUpdate();
        }
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

    
    private void loadUserProfile() {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT name, email, address, contact FROM Users WHERE userID = ?")) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userProfileLabel.setText("User: " + rs.getString("name") + " (" + rs.getString("email") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
