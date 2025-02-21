package librarysystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.table.JTableHeader;

public class BookReservations extends JFrame {
    private JTable booksTable, reservationsTable;
    private JButton notifyButton, closeButton;
    private JCheckBox showCancelled;
    private DefaultTableModel booksModel, reservationsModel;
    private Connection connection;
    
    public BookReservations() {
        setTitle("Book Reservations");
        setSize(800, 600);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        connection = Database.connect();

        // Title Label
        JLabel titleLabel = new JLabel("Book Reservations", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setBounds(0, 10, 800, 30);
        add(titleLabel);
        
        booksModel = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "Quantity", "Genre", "Location"}, 0);
        booksTable = new JTable(booksModel);
        styleTable(booksTable);
        JScrollPane booksScroll = new JScrollPane(booksTable);
        booksScroll.setBounds(20, 50, 750, 200);
        add(booksScroll);
        
        reservationsModel = new DefaultTableModel(new String[]{"ReservationID", "User ID", "Name", "Email", "Reservation Date", "Status"}, 0);
        reservationsTable = new JTable(reservationsModel);
        styleTable(reservationsTable);
        JScrollPane reservationsScroll = new JScrollPane(reservationsTable);
        reservationsScroll.setBounds(20, 270, 750, 200);
        add(reservationsScroll);
        
        notifyButton = createButton("Notify Next User", 300, 490);
        notifyButton.addActionListener(e -> notifyUser());
        add(notifyButton);
        
        closeButton = createButton("Close", 600, 490);
        closeButton.addActionListener(e -> dispose());
        add(closeButton);
        
        showCancelled = new JCheckBox("Show Cancelled");
        showCancelled.setBounds(20, 490, 150, 30);
        showCancelled.addActionListener(e -> loadReservations());
        add(showCancelled);
        
        loadBooks();
        booksTable.getSelectionModel().addListSelectionListener(e -> loadReservations());
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    // Create Button with Styling
    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBounds(x, y, 180, 35);

        // Default colors
        Color defaultBg = new Color(0x393939);
        Color defaultFg = Color.WHITE;
        Color hoverBg = Color.WHITE;
        Color hoverFg = new Color(0x393939);

        // Apply default styling
        button.setBackground(defaultBg);
        button.setForeground(defaultFg);
        button.setBorder(BorderFactory.createLineBorder(defaultBg, 2));
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

        return button;
    }

    private void styleTable(JTable table) {
        // Table Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setBackground(new Color(0x393939)); // Dark Gray
        header.setForeground(Color.WHITE);
        header.setOpaque(true);

        // Table Body Styling
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(0x393939));
        table.setGridColor(new Color(0xD3D3D3)); // Light Gray Grid
        table.setShowGrid(false); // Hide default grid lines
        table.setIntercellSpacing(new Dimension(0, 0)); // Remove default spacing

        // Selection Styling
        table.setSelectionBackground(new Color(0x393939));
        table.setSelectionForeground(Color.WHITE);

        // Borderless Look
        table.setBorder(BorderFactory.createEmptyBorder());
    }
    
    private void loadBooks() {
        try (
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT DISTINCT b.bookID, b.title, b.author, b.quantity, b.genre, b.location " +
                "FROM Books b " +
                "INNER JOIN Reservations r ON b.bookID = r.bookID " +
                "WHERE r.status != 'Cancelled'"
            )
        ) {
            booksModel.setRowCount(0);
            while (rs.next()) {
                booksModel.addRow(new Object[]{
                    rs.getInt("bookID"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("quantity"),
                    rs.getString("genre"),
                    rs.getString("location")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private void loadReservations() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) return;
        int bookID = (int) booksModel.getValueAt(selectedRow, 0);
        boolean showCancelledStatus = showCancelled.isSelected();
        
        try (PreparedStatement stmt = connection.prepareStatement("SELECT r.reservationID, r.userID, u.name, u.email, r.reservationDate, r.status FROM Reservations r JOIN Users u ON r.userID = u.userID WHERE r.bookID = ? " + (showCancelledStatus ? "" : "AND r.status != 'Cancelled'") + " ORDER BY r.reservationDate ASC")) {
            stmt.setInt(1, bookID);
            ResultSet rs = stmt.executeQuery();
            reservationsModel.setRowCount(0);
            while (rs.next()) {
                reservationsModel.addRow(new Object[]{rs.getInt("reservationID"), rs.getInt("userID"), rs.getString("name"), rs.getString("email"), rs.getDate("reservationDate"), rs.getString("status")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public String getUserEmail(String userID) {
        
        
        
        String email = null;
        try {
            String query = "SELECT email FROM users WHERE userID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                email = resultSet.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }
    
    private void notifyUser() {
        if (booksTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (reservationsModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No reservations for this book.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the first pending reservation
        int pendingIndex = -1;
        for (int i = 0; i < reservationsModel.getRowCount(); i++) {
            if ("Pending".equals(reservationsModel.getValueAt(i, 5))) { // 'Status' column is index 5
                pendingIndex = i;
                break;
            }
        }

        if (pendingIndex == -1) {
            JOptionPane.showMessageDialog(this, "No pending reservations.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String email = (String) reservationsModel.getValueAt(pendingIndex, 3); // Corrected email index
        String bookTitle = (String) booksModel.getValueAt(booksTable.getSelectedRow(), 1);
        int userID = (int) reservationsModel.getValueAt(pendingIndex, 1); // Corrected userID index

        // Ask for confirmation before proceeding
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to notify the next user for \"" + bookTitle + "\"?",
            "Confirm Notification",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return; // Do nothing if user selects No
        }
    
        try {
            NotifyReservation.sendEmail(email, bookTitle);

            // Update reservation status to 'Notified'
            try (PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE Reservations SET status = 'Notified' WHERE userID = ? AND bookID = ?")) {
                stmt.setInt(1, userID);
                stmt.setInt(2, (int) booksModel.getValueAt(booksTable.getSelectedRow(), 0));
                stmt.executeUpdate();
            }

            loadReservations(); // Refresh table

            JOptionPane.showMessageDialog(this, "Notification sent to " + email, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send email.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public static void main(String[] args) {
        new BookReservations();
    }
}
