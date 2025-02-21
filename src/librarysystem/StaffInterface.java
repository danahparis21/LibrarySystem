package librarysystem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;

public class StaffInterface extends JFrame {
     private JTable bookTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton, refreshButton, searchButton;
    private JTextField searchField;
    private JComboBox<String> searchFilter;
    private Connection connection;
    
    // Constructor
    public StaffInterface() {
        setTitle("Library Staff Interface");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        
       // Load the original image
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/header3.png"));

        // Scale the image to fit the desired size (1920x70)
        Image image = icon.getImage().getScaledInstance(1920, 70, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(image);

        // Apply the scaled image to JLabel
        JLabel header = new JLabel(scaledIcon);
        header.setBounds(0, 0, 1920, 70);
        add(header);
        
        JPanel panel = new JPanel();
                panel.setBounds(20, 100, 1200, 90);
                panel.setBackground(Color.WHITE);
                panel.setLayout(null);
                panel.setBorder(new LineBorder(Color.GRAY, 1));
                add(panel);
                
          
                
        searchField = new JTextField();
        searchField.setBounds(20, 30, 200, 50);
        panel.add(searchField);

       searchFilter = new JComboBox<>(new String[]{"Title", "Author", "Genre", "ISBN", "Publisher", "Publication Year", "Location", "Quantity"});
        searchFilter.setBounds(280, 30, 150, 50);
        searchFilter.setBackground(Color.WHITE); // White background
        searchFilter.setForeground(new Color(0x393939)); // Dark gray text
        searchFilter.setFont(new Font("Arial", Font.PLAIN, 16)); // Bigger font
        searchFilter.setBorder(BorderFactory.createLineBorder(new Color(0x393939), 2)); // Dark gray border

        panel.add(searchFilter);


    
        searchButton = createButton("Search", 450, 30, panel);
    
        
        
        
        

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "ISBN", "Genre", "Publisher", "Publication Year", "Quantity", "Location"}, 0);
        bookTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(bookTable);
        tableScroll.setBounds(20, 200, 1200, 350);
        styleTable(bookTable);
        add(tableScroll);

        //BUTTONS
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(20, 560, 1200, 90);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(null);
        buttonPanel.setBorder(new LineBorder(Color.GRAY, 1));
        add(buttonPanel);
        
        // Buttons
        addButton = createButton("Add Book", 50, 20, buttonPanel);
        updateButton = createButton("Update Book", 350, 20, buttonPanel);
        deleteButton = createButton("Delete Book", 650, 20, buttonPanel);
        refreshButton = createButton("Refresh", 950, 20, buttonPanel);
        
      
        // Connect to the database
        connection = Database.connect();

        // Sample data
        loadBookData();
        
        //OTHER BUTTONS
         //OTHER BUTTONS
        JPanel panel2 = new JPanel();
        panel2.setBounds(1250, 100, 250, 690);
        panel2.setBackground(Color.WHITE);
        panel2.setLayout(null);
        panel2.setBorder(new LineBorder(Color.GRAY, 1));
        add(panel2);
        
        
        JButton manageMembersButton = createButton("Manage Members", 25, 50, panel2);
        JButton borrowReturnButton = createButton("Borrow/Return", 25, 120, panel2);
        JButton bookReservationsButton = createButton("Book Reservations", 25, 190, panel2);
        JButton dataAnalyticsButton = createButton("Data Analytics", 25, 260, panel2);
        JButton logOut = createButton("Log Out", 25, 330, panel2);
        
      

        // Open Member Management Window
        manageMembersButton.addActionListener(e -> new ManageMembers().setVisible(true));

        
        borrowReturnButton.addActionListener(e -> new BorrowReturn().setVisible(true));
        bookReservationsButton.addActionListener(e -> new BookReservations().setVisible(true));
        dataAnalyticsButton.addActionListener(e -> new DataAnalytics().setVisible(true));



        refreshButton.addActionListener(e -> loadBookData());
        addButton.addActionListener(e -> new addBook().setVisible(true));
        
        updateButton.addActionListener(e -> updateBook());
        deleteButton.addActionListener(e -> deleteBook());
        searchButton.addActionListener(e -> searchBooks());
        
        logOut.addActionListener(e -> logOut());
        setVisible(true);
    }
    
        private void logOut(){
            new LogInForm();
            dispose();
        }
     private JLabel createLabel(String text, int x, int y, JPanel panel) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Times New Roman", Font.BOLD, 20));
        label.setBounds(x, y, 150, 30);
        panel.add(label);
        return label;
    }

    private JTextField createTextField(String text, int x, int y, JPanel panel) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        textField.setBounds(x, y, 100, 35);
        panel.add(textField);
        return textField;
    }

    private JButton createButton(String text, int x, int y, JPanel panel) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBounds(x, y, 200, 50);

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

        panel.add(button);
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
            String publisher = rs.getString("publisher");
            int publicationYear = rs.getInt("publicationYear");
            int quantity = rs.getInt("quantity");
            String location = rs.getString("location");

            tableModel.addRow(new Object[]{bookID, title, author, isbn, genre, publisher, publicationYear, quantity, location});
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
                    rs.getString("publisher"),
                    rs.getInt("publicationYear"),

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
            JOptionPane.showMessageDialog(this, "Select a book to update.");
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
