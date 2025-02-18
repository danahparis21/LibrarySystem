package librarysystem;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PayFineWindow extends JFrame {
    private double fineAmount;
    private int selectedRow;
    private int fineColumnIndex;
    private JTable borrowedBooksTable;

    public PayFineWindow(double fineAmount, int selectedRow, int fineColumnIndex) {
        this.fineAmount = fineAmount;
        this.selectedRow = selectedRow;
        this.fineColumnIndex = fineColumnIndex;
        
        // Set up the window
        setTitle("Pay Fine");
        setLayout(new FlowLayout());
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JLabel label = new JLabel("Fines to be paid: $" + fineAmount);
        JTextField paymentField = new JTextField(10);
        JButton payButton = new JButton("Pay Fine");

        payButton.addActionListener(e -> processPayment(paymentField));

        add(label);
        add(paymentField);
        add(payButton);
    }

    private void processPayment(JTextField paymentField) {
        double payment = 0.0;
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

        // Update the database with the new fine amount and status
        updateDatabase(remainingFine);
    }

    private void updateDatabase(double remainingFine) {
        int fineID = (int) borrowedBooksTable.getValueAt(selectedRow, 0);  // Assuming the fineID is in the first column
        String updateQuery = "UPDATE fines SET amount = ?, status = ? WHERE fineID = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/your_database", "your_username", "your_password");
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            // Set parameters for the query
            stmt.setDouble(1, remainingFine <= 0 ? 0.0 : remainingFine); // Update the fine amount
            stmt.setString(2, remainingFine <= 0 ? "Paid" : "Unpaid");  // Update the status
            stmt.setInt(3, fineID);  // Identify the fine record by fineID

            // Execute the update
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Database updated successfully.");
            } else {
                System.out.println("Failed to update the database.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}
