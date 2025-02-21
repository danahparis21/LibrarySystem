package librarysystem;

import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class addBook extends JFrame {
    private JTextField titleField, authorField, isbnField, genreField, publisherField, yearField, quantityField, locationField;

    public addBook() {
        setTitle("Add Book");
        setSize(450, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE); // White background

        
        JLabel header = new JLabel("Add Book Details", SwingConstants.CENTER);
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

        JButton addButton = createStyledButton("Add Book", 130, 400);
        add(addButton);
        addButton.addActionListener(this::addBook);

        
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
   
   

    private void addBook(ActionEvent e) {
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
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO books (title, author, isbn, genre, publisher, publicationYear, quantity, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                stmt.setString(1, title);
                stmt.setString(2, author);
                stmt.setString(3, isbn);
                stmt.setString(4, genre);
                stmt.setString(5, publisher);
                stmt.setInt(6, publicationYear);
                stmt.setInt(7, quantity);
                stmt.setString(8, location);

                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Book added successfully!");
                dispose(); // Close the window
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid year or quantity format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding book.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new addBook();
    }
}
