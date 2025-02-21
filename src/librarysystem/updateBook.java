package librarysystem;

import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class updateBook extends JFrame {
    private JTextField titleField, authorField, isbnField, genreField, publisherField, yearField, quantityField, locationField;
    private int bookID;

    public updateBook(int bookID) {
        this.bookID = bookID;
        setTitle("Update Book");
        setSize(450, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE); // White background
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel header = new JLabel("Update Book Details", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.setBounds(0, 10, 450, 30);
        header.setForeground(new Color(50, 50, 50));
        add(header);

        addLabelAndField("Title:", 50, titleField = new JTextField());
        addLabelAndField("Author:", 90, authorField = new JTextField());
        addLabelAndField("ISBN:", 130, isbnField = new JTextField());
        addLabelAndField("Genre:", 170, genreField = new JTextField());
        addLabelAndField("Publisher:", 210, publisherField = new JTextField());
        addLabelAndField("Publication Year:", 250, yearField = new JTextField());
        addLabelAndField("Quantity:", 290, quantityField = new JTextField());
        addLabelAndField("Location:", 330, locationField = new JTextField());

        JButton updateButton = createStyledButton("Update Book", 130, 400);
        add(updateButton);
        updateButton.addActionListener(this::updateBook);

        loadBookDetails();
        setVisible(true);
    }
   
    private void addLabelAndField(String labelText, int y, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setBounds(30, y, 120, 25);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(label);

        field.setBounds(160, y, 230, 30);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(field);
    }

    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 180, 40);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(0x393939));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(new Color(0x393939), 2));
        button.setFocusPainted(false);
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(new Color(0x393939));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0x393939));
                button.setForeground(Color.WHITE);
            }
        });

        return button;
    }
   
   
   

    private void loadBookDetails() {
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE bookID = ?")) {
            stmt.setInt(1, bookID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                titleField.setText(rs.getString("title"));
                authorField.setText(rs.getString("author"));
                isbnField.setText(rs.getString("isbn"));
                genreField.setText(rs.getString("genre"));
                publisherField.setText(rs.getString("publisher"));
                yearField.setText(String.valueOf(rs.getInt("publicationYear")));
                quantityField.setText(String.valueOf(rs.getInt("quantity")));
                locationField.setText(rs.getString("location"));
            } else {
                JOptionPane.showMessageDialog(this, "Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading book details.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook(ActionEvent e) {
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();
        String genre = genreField.getText();
        String publisher = publisherField.getText();
        String yearText = yearField.getText();
        String quantityText = quantityField.getText();
        String location = locationField.getText();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || genre.isEmpty() || publisher.isEmpty() ||
            yearText.isEmpty() || quantityText.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int publicationYear = Integer.parseInt(yearText);
            int quantity = Integer.parseInt(quantityText);

            try (Connection conn = Database.connect();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE books SET title = ?, author = ?, isbn = ?, genre = ?, publisher = ?, publicationYear = ?, quantity = ?, location = ? WHERE bookID = ?")) {
                stmt.setString(1, title);
                stmt.setString(2, author);
                stmt.setString(3, isbn);
                stmt.setString(4, genre);
                stmt.setString(5, publisher);
                stmt.setInt(6, publicationYear);
                stmt.setInt(7, quantity);
                stmt.setString(8, location);
                stmt.setInt(9, bookID);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book updated successfully!");
                dispose(); // Close the window
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid year or quantity format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating book.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
}
