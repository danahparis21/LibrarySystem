package librarysystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class PayFineWindow extends JFrame {
    private double fineAmount;
    private int selectedRow;
    private int fineColumnIndex;
    private JTable borrowedBooksTable;
     private Connection connection;

    public PayFineWindow(double fineAmount, int selectedRow, int fineColumnIndex,  JTable borrowedBooksTable) {
        connection = Database.connect();
        this.fineAmount = fineAmount;
        this.selectedRow = selectedRow;
        this.fineColumnIndex = fineColumnIndex;
        this.borrowedBooksTable = borrowedBooksTable;

        // Set up the window
        setTitle("Pay Fine");
        setLayout(new FlowLayout());
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel fineLabel = new JLabel("To be paid: $" + fineAmount);
        JTextField paymentField = new JTextField(10);
        JLabel changeLabel = new JLabel("Change: $0.00");
        JButton payButton = new JButton("Pay Fine");
        JButton closeButton = new JButton("Close");

        payButton.addActionListener(e -> processPayment(paymentField, fineLabel, changeLabel));
        closeButton.addActionListener(e -> dispose()); // Closes the window

        add(fineLabel);
        add(paymentField);
        add(changeLabel);
        add(payButton);
        add(closeButton);
    }

    private void processPayment(JTextField paymentField, JLabel fineLabel, JLabel changeLabel) {
        double payment;
        try {
            payment = Double.parseDouble(paymentField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter a numeric value.");
            return;
        }

        if (payment < 0) {
            JOptionPane.showMessageDialog(this, "Invalid payment amount. Please enter a valid amount.");
            return;
        }

        double remainingFine = fineAmount - payment;
        double change = payment > fineAmount ? payment - fineAmount : 0.0;

        if (remainingFine <= 0) {
            // Fine fully paid, mark book as returned and remove overdue status
            borrowedBooksTable.setValueAt("0.0", selectedRow, fineColumnIndex); // Set fine to $0
            borrowedBooksTable.setValueAt("Returned", selectedRow, 3); // Assuming status is at index 3
            JOptionPane.showMessageDialog(this, "Fine fully paid! The book is now returned.");
        } else {
            // Fine partially paid, update remaining fine
            borrowedBooksTable.setValueAt(String.valueOf(remainingFine), selectedRow, fineColumnIndex);
            JOptionPane.showMessageDialog(this, "Partial payment made. Remaining fine: $" + remainingFine);
        }

        // Update UI
        fineLabel.setText("To be paid: $" + Math.max(remainingFine, 0));
        changeLabel.setText("Change: $" + change);

        // Update the database
        updateDatabase(remainingFine);

        // Generate a receipt
        generateReceipt(payment, remainingFine, change);
    }

    private void updateDatabase(double remainingFine) {
    int borrowID = (int) borrowedBooksTable.getValueAt(selectedRow, 0); // Correctly gets Borrow ID
    java.util.Date utilDate = new java.util.Date();
    java.sql.Date today = new java.sql.Date(utilDate.getTime());

    // Get the correct Fine ID
    int fineID = -1;
    String fineQuery = "SELECT fineID FROM Fines WHERE userID = (SELECT userID FROM BorrowedBooks WHERE borrowID = ?)";

    try (PreparedStatement fineStmt = connection.prepareStatement(fineQuery)) {
        fineStmt.setInt(1, borrowID);
        ResultSet rs = fineStmt.executeQuery();
        if (rs.next()) {
            fineID = rs.getInt("fineID");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching fine ID: " + e.getMessage());
        return;
    }

    if (fineID == -1) {
        JOptionPane.showMessageDialog(this, "No fine found for Borrow ID: " + borrowID);
        return;
    }

    // Update Fines Table
    String updateFineQuery = "UPDATE Fines SET amount = ?, status = ? WHERE fineID = ?";
    try (PreparedStatement fineStmt = connection.prepareStatement(updateFineQuery)) {
        fineStmt.setDouble(1, remainingFine <= 0 ? 0.0 : remainingFine);
        fineStmt.setString(2, remainingFine <= 0 ? "Paid" : "Unpaid");
        fineStmt.setInt(3, fineID);
        int fineUpdated = fineStmt.executeUpdate();
        System.out.println("Fines Updated: " + fineUpdated);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error updating fines: " + e.getMessage());
    }

    // If fine is fully paid, update BorrowedBooks status
    if (remainingFine <= 0) {
        String updateBorrowedQuery = "UPDATE BorrowedBooks SET returnDate = ?, status = 'Returned' WHERE borrowID = ?";
        try (PreparedStatement borrowStmt = connection.prepareStatement(updateBorrowedQuery)) {
            borrowStmt.setDate(1, today);
            borrowStmt.setInt(2, borrowID);
            int borrowedUpdated = borrowStmt.executeUpdate();
            System.out.println("BorrowedBooks Updated: " + borrowedUpdated);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating borrowed book status: " + e.getMessage());
        }
    }
}




    private void generateReceipt(double payment, double remainingFine, double change) {
        String receipt = "---- Library Fine Payment Receipt ----\n" +
                "Fine Amount: $" + fineAmount + "\n" +
                "Payment Made: $" + payment + "\n" +
                "Remaining Fine: $" + Math.max(remainingFine, 0) + "\n" +
                "Change: $" + change + "\n" +
                "--------------------------------------";

        JOptionPane.showMessageDialog(this, receipt, "Payment Receipt", JOptionPane.INFORMATION_MESSAGE);
    }
}
