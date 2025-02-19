package librarysystem;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableModel;

public class BorrowReturn extends JFrame {

    private JTextField userIDField, bookIDField;
    private JButton borrowButton, returnButton, renewButton, payFineButton, refreshButton;
    private JLabel statusLabel;
    private JTable booksTable, borrowedBooksTable;
    private DefaultTableModel booksModel, borrowedModel;
    private Connection connection;
    String userPhone ;

    public BorrowReturn() {
        setTitle("Borrow/Return Books");
        setSize(700, 500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        connection = Database.connect();

        JLabel userIDLabel = new JLabel("User ID:");
        userIDLabel.setBounds(30, 30, 100, 25);
        add(userIDLabel);

        userIDField = new JTextField();
        userIDField.setBounds(140, 30, 150, 25);
        add(userIDField);
        userIDField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                loadBorrowedBooks();
            }
        });

        JLabel bookIDLabel = new JLabel("Book ID:");
        bookIDLabel.setBounds(30, 70, 100, 25);
        add(bookIDLabel);

        bookIDField = new JTextField();
        bookIDField.setBounds(140, 70, 150, 25);
        add(bookIDField);

        borrowButton = new JButton("Borrow Book");
        borrowButton.setBounds(30, 110, 120, 30);
        add(borrowButton);

        returnButton = new JButton("Return Book");
        returnButton.setBounds(160, 110, 120, 30);
        add(returnButton);

        renewButton = new JButton("Renew Book");
        renewButton.setBounds(290, 110, 120, 30);
        add(renewButton);

        payFineButton = new JButton("Pay Fine");
        payFineButton.setBounds(30, 160, 120, 30);
        add(payFineButton);
        
        JButton notifyButton = new JButton("Notify");
        notifyButton.setBounds(290, 160, 120, 30);
        add(notifyButton);
        
        notifyButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedRow = borrowedBooksTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an overdue book.");
            return;
        }

        // Get the user's details (email, phone number)
        String userID = borrowedBooksTable.getValueAt(selectedRow, 1).toString();
        String bookTitle = borrowedBooksTable.getValueAt(selectedRow, 2).toString();
        String userEmail = getUserEmail(userID);
        System.out.println("User Email: " + userEmail);  // Debugging

        String userPhone = "+639605574527";  // Use fixed phone number directly

        // Send the notifications
        if (userEmail != null && userPhone != null) {
            sendEmail(userEmail, bookTitle);
            sendSMS(userPhone, bookTitle);
            JOptionPane.showMessageDialog(null, "Notification sent to the user.");
        } else {
            JOptionPane.showMessageDialog(null, "User's contact details are missing.");
        }
    }
});




        statusLabel = new JLabel();
        statusLabel.setBounds(30, 200, 400, 25);
        add(statusLabel);

        // Books Table
        booksModel = new DefaultTableModel(new String[]{"Book ID", "Title", "Stock"}, 0);
        booksTable = new JTable(booksModel);
        JScrollPane bookScrollPane = new JScrollPane(booksTable);
        bookScrollPane.setBounds(30, 240, 300, 200);
        add(bookScrollPane);
        booksTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = booksTable.getSelectedRow();
                bookIDField.setText(booksModel.getValueAt(row, 0).toString());
            }
        });

        // Borrowed Books Table
        borrowedModel = new DefaultTableModel(new String[]{"Borrow ID", "User ID", "Book Title", "Borrow Date", "Due Date", "Status", "Fine"}, 0);
        borrowedBooksTable = new JTable(borrowedModel);
        JScrollPane borrowedScrollPane = new JScrollPane(borrowedBooksTable);
        borrowedScrollPane.setBounds(350, 240, 320, 200);
        add(borrowedScrollPane);

        borrowedBooksTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = borrowedBooksTable.getSelectedRow();
                String status = borrowedModel.getValueAt(row, 3).toString();
                if ("Overdue".equals(status)) {
                    JOptionPane.showMessageDialog(null, "This book is overdue! A fine is applied.");
                }
            }
        });
        
        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(150, 160, 120, 30);
        add(refreshButton);
        
        

        borrowButton.addActionListener(e -> borrowBook());
        returnButton.addActionListener(e -> returnBook());
        renewButton.addActionListener(e -> renewBook());
        payFineButton.addActionListener(e -> payFine());
        refreshButton.addActionListener(e -> {
        loadBooks();
        if (!userIDField.getText().trim().isEmpty()) {
            loadBorrowedBooks();
        } else {
            borrowedModel.setRowCount(0); // Clear table if no user ID
        }
    });



        loadBooks();
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


//    private String getUserPhone(String userID) {
//        try {
//            PreparedStatement stmt = connection.prepareStatement("SELECT contact FROM users WHERE userID = ?");
//            stmt.setString(1, userID);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()) {
//                String phone = rs.getString("contact");
//                 System.out.println("User phone: " + phone);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

        private void sendEmail(String userEmail, String bookTitle) {
        // Call the static method from the SendEmail class
        SendEmail.sendEmail(userEmail, bookTitle);
    }


    private void sendSMS(String userPhone, String bookTitle) {
        TwilioSMS.sendSMS("+639605574527", bookTitle);
    }



    private void borrowBook() {
        try {
            String userID = userIDField.getText();
            String bookID = bookIDField.getText();
            String borrowDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            //String dueDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000));
            String dueDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() + 1L * 24 * 60 * 60 * 1000));

            
            
            PreparedStatement checkAvailability = connection.prepareStatement("SELECT quantity FROM Books WHERE bookID = ?");
            checkAvailability.setString(1, bookID);
            ResultSet rs = checkAvailability.executeQuery();

            if (rs.next() && rs.getInt("quantity") > 0) {
                PreparedStatement borrowStmt = connection.prepareStatement("INSERT INTO BorrowedBooks (userID, bookID, borrowDate, dueDate) VALUES (?, ?, ?, ?)");
                borrowStmt.setString(1, userID);
                borrowStmt.setString(2, bookID);
                borrowStmt.setString(3, borrowDate);
                borrowStmt.setString(4, dueDate);
                borrowStmt.executeUpdate();

                PreparedStatement updateStock = connection.prepareStatement("UPDATE Books SET quantity = quantity - 1 WHERE bookID = ?");
                updateStock.setString(1, bookID);
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
            String userID = userIDField.getText();
            String bookID = bookIDField.getText();
            String newDueDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000));

            PreparedStatement renewStmt = connection.prepareStatement("UPDATE BorrowedBooks SET dueDate = ? WHERE userID = ? AND bookID = ? AND returnDate IS NULL");
            renewStmt.setString(1, newDueDate);
            renewStmt.setString(2, userID);
            renewStmt.setString(3, bookID);
            int updated = renewStmt.executeUpdate();

            if (updated > 0) {
                statusLabel.setText("Book renewed successfully! New due date: " + newDueDate);
            } else {
                statusLabel.setText("Renewal failed. Check if the book is borrowed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error renewing book.");
        }
    }

    private void loadBooks() {
        booksModel.setRowCount(0);
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT bookID, title, quantity FROM Books");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                booksModel.addRow(new Object[]{rs.getString("bookID"), rs.getString("title"), rs.getInt("quantity")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBorrowedBooks() {
        borrowedModel.setRowCount(0);  // Clears the table
        try {
            String userID = userIDField.getText();  // Get the userID from the text field
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT BB.borrowID, BB.userID,  B.title, BB.borrowDate, BB.dueDate, BB.status FROM BorrowedBooks BB "
                    + "JOIN Books B ON BB.bookID = B.bookID WHERE BB.userID = ? AND BB.returnDate IS NULL");
            stmt.setString(1, userID);  // Set the userID in the query
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String status = rs.getString("status");
                String dueDateStr = rs.getString("dueDate");
                int fine = 0;

                // Check if overdue
                Date dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateStr);
                if (new Date().after(dueDate)) {
                    status = "Overdue";
                    long daysOverdue = (new Date().getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24);
                    fine = (int) daysOverdue;  // $1 per day fine
                    updateStatusToOverdue(userID, rs.getString("title"));  // You may want to update status to Overdue in the database
                }

                // Add the borrowID to the table row
                borrowedModel.addRow(new Object[]{rs.getInt("borrowID"),rs.getInt("userID"), rs.getString("title"), rs.getString("borrowDate"), dueDateStr, status, "$" + fine});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateStatusToOverdue(String userID, String bookTitle) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE BorrowedBooks SET status = 'Overdue' WHERE userID = ? AND bookID = "
                    + "(SELECT bookID FROM Books WHERE title = ?)");
            stmt.setString(1, userID);
            stmt.setString(2, bookTitle);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   private void returnBook() {
    try {
        // Get the selected row index
        int selectedRow = borrowedBooksTable.getSelectedRow();

        if (selectedRow == -1) {
            // No row selected
            statusLabel.setText("Please select a borrowed book to return.");
            return;
        }

        // Get the borrowID from the selected row (from the table displayed)
        // Since borrowID is stored as an Integer in the table, no need to cast it to String
        int borrowID = (Integer) borrowedBooksTable.getValueAt(selectedRow, 0);  // Directly cast to Integer
        System.out.println("Selected borrowID: " + borrowID);
        
        // Query the database to get the userID and bookID using the borrowID
        String userID = "";
        String bookID = "";

        PreparedStatement fetchDetails = connection.prepareStatement(
                "SELECT userID, bookID FROM borrowedbooks WHERE borrowID = ?");
        fetchDetails.setInt(1, borrowID);  // Use the correct integer parameter for borrowID
        ResultSet resultSet = fetchDetails.executeQuery();

        if (resultSet.next()) {
            userID = resultSet.getString("userID");
            bookID = resultSet.getString("bookID");
        }

        if (userID.isEmpty() || bookID.isEmpty()) {
            statusLabel.setText("Error fetching book details. Please try again.");
            return;
        }

        // Format the return date
        String returnDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Update the return date and status in the database
        PreparedStatement returnStmt = connection.prepareStatement(
                "UPDATE BorrowedBooks SET returnDate = ?, status = 'Returned' WHERE borrowID = ?");
        returnStmt.setString(1, returnDate);
        returnStmt.setInt(2, borrowID);  // Use the integer borrowID for the update
        int updated = returnStmt.executeUpdate();

        // Check how many rows were updated
        System.out.println("Rows updated: " + updated);

        if (updated > 0) {
            // Update the stock quantity of the book in the Books table
            PreparedStatement updateStock = connection.prepareStatement(
                    "UPDATE Books SET quantity = quantity + 1 WHERE bookID = ?");
            updateStock.setString(1, bookID);
            updateStock.executeUpdate();

            // Show success message
            statusLabel.setText("Book returned successfully!");
            loadBorrowedBooks();  // Reload the list of borrowed books
        } else {
            // Show error message
            statusLabel.setText("Book return failed. Check if the book was borrowed.");
        }
    } catch (SQLException e) {
        System.out.println("Error occurred: " + e.getMessage());
        e.printStackTrace();
    } catch (NumberFormatException e) {
        System.out.println("Invalid borrowID format: " + e.getMessage());
    }
}









    private void payFine() {
        int selectedRow = borrowedBooksTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a borrowed book to pay fines.");
            return;
        }

        // Dynamically retrieve the index of the "Fine" column
        int fineColumnIndex = borrowedBooksTable.getColumnModel().getColumnIndex("Fine");

        // Retrieve the fine amount as a String
        String fineAmountString = (String) borrowedBooksTable.getValueAt(selectedRow, fineColumnIndex);

        // If the fine value is zero (or empty), show a message that no fine needs to be paid
        if (fineAmountString == null || fineAmountString.isEmpty() || fineAmountString.equals("0") || fineAmountString.equals("0.0")) {
            JOptionPane.showMessageDialog(null, "No fines need to be paid. Thank you!");
            return;
        }

        double fineAmount = 0.0;
        try {
            fineAmount = Double.parseDouble(fineAmountString);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid fine amount in the table. Please check the data.");
            return;
        }

        // Open the payFine window and pass necessary information
        new PayFineWindow(fineAmount, selectedRow, fineColumnIndex).setVisible(true);
    }




    public static void main(String[] args) {
        new BorrowReturn().setVisible(true);
    }
}
