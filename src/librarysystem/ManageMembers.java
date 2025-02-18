package librarysystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ManageMembers extends JFrame {
    private JTextField searchField, nameField, emailField, contactField, addressField;
    private JButton viewButton, searchButton, updateButton, deleteButton, closeButton;
    private JTable memberTable;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;
     private Connection connection;

    public ManageMembers() {
        setTitle("ManageMembers");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        setLayout(null);  // Using null layout for precise placement of components
             // Connect to the database
        connection = Database.connect();
        // Initialize components
        searchField = new JTextField();
        searchButton = new JButton("Search");
        viewButton = new JButton("View All Members");
        updateButton = new JButton("Update Member");
        deleteButton = new JButton("Delete Member");
        
        nameField = new JTextField();
        emailField = new JTextField();
        contactField = new JTextField();
        addressField = new JTextField();

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Contact", "Address", "Role"}, 0);
        memberTable = new JTable(tableModel);
        tableScroll = new JScrollPane(memberTable);

        // Place components on panel
        searchField.setBounds(50, 20, 200, 25);
        searchButton.setBounds(260, 20, 100, 25);
        viewButton.setBounds(50, 60, 150, 25);
        updateButton.setBounds(220, 60, 150, 25);
        deleteButton.setBounds(50, 100, 150, 25);
       

        nameField.setBounds(50, 140, 200, 25);
        emailField.setBounds(50, 180, 200, 25);
        contactField.setBounds(50, 220, 200, 25);
        addressField.setBounds(50, 260, 200, 25);

        tableScroll.setBounds(300, 100, 400, 200);

        // Add components to panel
        add(searchField);
        add(searchButton);
        add(viewButton);
        add(updateButton);
        add(deleteButton);
        add(nameField);
        add(emailField);
        add(contactField);
        add(addressField);
        add(tableScroll);

        // Add action listeners
        searchButton.addActionListener(e -> searchMember());
        viewButton.addActionListener(e -> viewMembers());
        updateButton.addActionListener(e -> updateMember());
        deleteButton.addActionListener(e -> deleteMember());
     
        closeButton = new JButton("Close");
        closeButton.setBounds(50, 300, 150, 25); // Position it as desired
        closeButton.addActionListener(e-> dispose()); // Exit the application when clicked
        add(closeButton); // Add it to the panel
        
        setVisible(true);
    }

    // Search member based on search input
    private void searchMember() {
        String searchQuery = searchField.getText();
        try {
            String query = "SELECT * FROM users WHERE name LIKE ? OR email LIKE ? OR contact LIKE ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, "%" + searchQuery + "%");
            ps.setString(2, "%" + searchQuery + "%");
            ps.setString(3, "%" + searchQuery + "%");
            ResultSet rs = ps.executeQuery();
            tableModel.setRowCount(0); // Clear existing table rows
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("userID"), rs.getString("name"), rs.getString("email"), rs.getString("contact"), rs.getString("address"), rs.getString("role")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all members
    private void viewMembers() {
        try {String query = "SELECT * FROM users";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            tableModel.setRowCount(0); // Clear existing table rows
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("userID"), rs.getString("name"), rs.getString("email"), rs.getString("contact"), rs.getString("address"), rs.getString("role")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update member details
    private void updateMember() {
        // Get the values from the text fields
        String name = nameField.getText();
        String email = emailField.getText();
        String contact = contactField.getText();
        String address = addressField.getText();

        // Check if all fields are filled
        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields before updating.");
            return; // Don't proceed with the update if any field is empty
        }

        // Get the selected row
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow >= 0) {
            int userID = (int) tableModel.getValueAt(selectedRow, 0);

            // Perform the update operation
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "password")) {
                String query = "UPDATE users SET name = ?, email = ?, contact = ?, address = ? WHERE userID = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, contact);
                ps.setString(4, address);
                ps.setInt(5, userID);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Member details updated successfully!");
                viewMembers(); // Refresh table after update
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




    // Delete selected member
    private void deleteMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow >= 0) {
            int userID = (int) tableModel.getValueAt(selectedRow, 0);
            try {   String query = "DELETE FROM users WHERE userID = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, userID);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Member deleted successfully!");
                viewMembers(); // Refresh table after deletion
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
     public static void main(String[] args) {
        new ManageMembers();
    }

}
