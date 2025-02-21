package librarysystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ManageMembers extends JFrame {
    private JTextField searchField, nameField, emailField, contactField, addressField;
    private JButton viewButton, searchButton, updateButton, deleteButton, closeButton;
    private JTable memberTable;
    private JScrollPane tableScroll;
    private DefaultTableModel tableModel;
     private Connection connection;

  

    public ManageMembers() {
        setTitle("Manage Members");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);
        setLocationRelativeTo(null);

        connection = Database.connect(); // Connect to database

        // Title Label
        JLabel titleLabel = new JLabel("MANAGE MEMBERS");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0x393939));
        titleLabel.setBounds(350, 10, 300, 30);
        add(titleLabel);

        // Search Field
        searchField = new JTextField();
        searchField.setBounds(50, 50, 250, 30);
        add(searchField);

        // Buttons
        searchButton = createButton("Search", 320, 50);
        viewButton = createButton("View All Members", 50, 100);
        deleteButton = createButton("Delete Member", 220, 100);
        closeButton = createButton("Close", 50, 500); // Close button at the bottom

        add(searchButton);
        add(viewButton);
        add(deleteButton);
        add(closeButton);

        // Table Model & Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Contact", "Address", "Role"}, 0);
        memberTable = new JTable(tableModel);
        styleTable(memberTable);

        // Scroll Pane (Table)
        tableScroll = new JScrollPane(memberTable);
        tableScroll.setBounds(50, 150, 800, 330); // Made the table bigger
        add(tableScroll);

        // Action Listeners
        searchButton.addActionListener(e -> searchMember());
        viewButton.addActionListener(e -> viewMembers());
        deleteButton.addActionListener(e -> deleteMember());
        closeButton.addActionListener(e -> dispose()); // Close window when clicked

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

//    // Update member details
//    private void updateMember() {
//        // Get the values from the text fields
//        String name = nameField.getText();
//        String email = emailField.getText();
//        String contact = contactField.getText();
//        String address = addressField.getText();
//
//        // Check if all fields are filled
//        if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || address.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Please fill in all fields before updating.");
//            return; // Don't proceed with the update if any field is empty
//        }
//
//        // Get the selected row
//        int selectedRow = memberTable.getSelectedRow();
//        if (selectedRow >= 0) {
//            int userID = (int) tableModel.getValueAt(selectedRow, 0);
//
//            // Perform the update operation
//            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "password")) {
//                String query = "UPDATE users SET name = ?, email = ?, contact = ?, address = ? WHERE userID = ?";
//                PreparedStatement ps = conn.prepareStatement(query);
//                ps.setString(1, name);
//                ps.setString(2, email);
//                ps.setString(3, contact);
//                ps.setString(4, address);
//                ps.setInt(5, userID);
//                ps.executeUpdate();
//                JOptionPane.showMessageDialog(this, "Member details updated successfully!");
//                viewMembers(); // Refresh table after update
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    //}

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
