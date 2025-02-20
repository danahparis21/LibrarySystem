package librarysystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

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
        connection = Database.connect();

        booksModel = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "ISBN", "Genre", "Location"}, 0);
        booksTable = new JTable(booksModel);
        JScrollPane booksScroll = new JScrollPane(booksTable);
        booksScroll.setBounds(20, 20, 750, 200);
        add(booksScroll);
        
        reservationsModel = new DefaultTableModel(new String[]{"ReservationID","User ID", "Name", "Email", "Reservation Date", "Status"}, 0);
        reservationsTable = new JTable(reservationsModel);
        JScrollPane reservationsScroll = new JScrollPane(reservationsTable);
        reservationsScroll.setBounds(20, 250, 750, 200);
        add(reservationsScroll);
        
        notifyButton = new JButton("Notify Next User");
        notifyButton.setBounds(300, 470, 200, 30);
        notifyButton.addActionListener(e -> notifyUser());
        add(notifyButton);
        
        closeButton = new JButton("Close");
        closeButton.setBounds(600, 470, 100, 30);
        closeButton.addActionListener(e -> dispose());
        add(closeButton);
        
        
        showCancelled = new JCheckBox("Show Cancelled");
        showCancelled.setBounds(20, 470, 150, 30);
        showCancelled.addActionListener(e -> loadReservations());
        add(showCancelled);
        
        loadBooks();
        booksTable.getSelectionModel().addListSelectionListener(e -> loadReservations());
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private void loadBooks() {
        try (
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT bookID, title, author, ISBN, genre, location FROM Books WHERE quantity = 0")) {
            booksModel.setRowCount(0);
            while (rs.next()) {
                booksModel.addRow(new Object[]{rs.getInt("bookID"), rs.getString("title"), rs.getString("author"), rs.getString("ISBN"), rs.getString("genre"), rs.getString("location")});
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
