package librarysystem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StaffInterface extends JFrame {
     private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton, refreshButton, searchButton, bookReservationsButton;
    private JTextField searchField;
    private JComboBox<String> searchFilter;
    private Connection connection;
    
    // Constructor
    public StaffInterface() {
        setTitle("Library Staff Interface");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "ISBN", "Genre", "Quantity", "Location"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(bookTable);
        tableScroll.setBounds(30, 30, 700, 300);
        add(tableScroll);

        // Add button
        addButton = new JButton("Add Book");
        addButton.setBounds(30, 350, 120, 30);
        add(addButton);

        // Update button
        updateButton = new JButton("Update Book");
        updateButton.setBounds(170, 350, 120, 30);
        add(updateButton);

        // Delete button
        deleteButton = new JButton("Delete Book");
        deleteButton.setBounds(310, 350, 120, 30);
        add(deleteButton);
        
        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(450, 350, 120, 30);
        add(refreshButton);
        // Connect to the database
        connection = Database.connect();

        // Sample data
        loadBookData();
        
        searchField = new JTextField();
        searchField.setBounds(30, 400, 200, 30);
        add(searchField);

        searchFilter = new JComboBox<>(new String[]{"Title", "Author", "Genre", "ISBN", "Location", "Quantity"});
        searchFilter.setBounds(240, 400, 120, 30);
        add(searchFilter);

        searchButton = new JButton("Search");
        searchButton.setBounds(380, 400, 100, 30);
        add(searchButton);
        // Member Management button
        JButton manageMembersButton = new JButton("Manage Members");
        manageMembersButton.setBounds(30, 450, 160, 30);
        add(manageMembersButton);

        // Borrow & Return button
        JButton borrowReturnButton = new JButton("Borrow/Return");
        borrowReturnButton.setBounds(210, 450, 160, 30);
        add(borrowReturnButton);
        
        // Borrow & Return button
        JButton bookReservationsButton = new JButton("Book Reservations");
        bookReservationsButton.setBounds(390, 450, 160, 30);
        add(bookReservationsButton);

        // Open Member Management Window
        manageMembersButton.addActionListener(e -> new ManageMembers().setVisible(true));

        
        borrowReturnButton.addActionListener(e -> new BorrowReturn().setVisible(true));
        bookReservationsButton.addActionListener(e -> new BookReservations().setVisible(true));



        refreshButton.addActionListener(e -> loadBookData());
        
        updateButton.addActionListener(e -> updateBook());
        deleteButton.addActionListener(e -> deleteBook());
        searchButton.addActionListener(e -> searchBooks());

        setVisible(true);
    }

    // Sample method to load some data into the table
    // Load book data from the database
private void loadBookData() {
    try {
        // Clear existing rows before adding new data
        tableModel.setRowCount(0);

        String query = "SELECT * FROM books";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            int bookID = rs.getInt("bookID");
            String title = rs.getString("title");
            String author = rs.getString("author");
            String isbn = rs.getString("ISBN");
            String genre = rs.getString("genre");
            int quantity = rs.getInt("quantity");
            String location = rs.getString("location");

            tableModel.addRow(new Object[]{bookID, title, author, isbn, genre, quantity, location});
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


private void searchBooks() {
        String keyword = searchField.getText();
        String filter = searchFilter.getSelectedItem().toString().toLowerCase();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.");
            return;
        }

        String query = "SELECT * FROM books WHERE " + filter + " LIKE ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("bookID"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("ISBN"),
                    rs.getString("genre"),
                    rs.getInt("quantity"),
                    rs.getString("location")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
   
    // Logic for updating a book

    private void updateBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            int bookID = (int) tableModel.getValueAt(selectedRow, 0);
            
            
            new updateBook(bookID).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Select a room to update.");
        }
        }
    
            
    

    // Logic for deleting a book
    private void deleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            int bookID = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?");
            if (confirm == JOptionPane.YES_OPTION) {
                String query = "DELETE FROM books WHERE bookID = ?";
                try {
                    PreparedStatement stmt = connection.prepareStatement(query);
                    stmt.setInt(1, bookID);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Book deleted successfully!");

                    // Reload table data after deleting the book
                    tableModel.setRowCount(0);
                    loadBookData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
        }
    }

    public static void main(String[] args) {
        new StaffInterface();
    }
}
