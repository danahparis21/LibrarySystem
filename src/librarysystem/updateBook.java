package librarysystem;

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
        setSize(400, 400);
        setLayout(null);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setBounds(30, 30, 100, 25);
        add(titleLabel);

        titleField = new JTextField();
        titleField.setBounds(150, 30, 200, 25);
        add(titleField);

        JLabel authorLabel = new JLabel("Author:");
        authorLabel.setBounds(30, 70, 100, 25);
        add(authorLabel);

        authorField = new JTextField();
        authorField.setBounds(150, 70, 200, 25);
        add(authorField);

        JLabel isbnLabel = new JLabel("ISBN:");
        isbnLabel.setBounds(30, 110, 100, 25);
        add(isbnLabel);

        isbnField = new JTextField();
        isbnField.setBounds(150, 110, 200, 25);
        add(isbnField);

        JLabel genreLabel = new JLabel("Genre:");
        genreLabel.setBounds(30, 150, 100, 25);
        add(genreLabel);

        genreField = new JTextField();
        genreField.setBounds(150, 150, 200, 25);
        add(genreField);

        JLabel publisherLabel = new JLabel("Publisher:");
        publisherLabel.setBounds(30, 190, 100, 25);
        add(publisherLabel);

        publisherField = new JTextField();
        publisherField.setBounds(150, 190, 200, 25);
        add(publisherField);

        JLabel yearLabel = new JLabel("Publication Year:");
        yearLabel.setBounds(30, 230, 120, 25);
        add(yearLabel);

        yearField = new JTextField();
        yearField.setBounds(150, 230, 200, 25);
        add(yearField);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setBounds(30, 270, 100, 25);
        add(quantityLabel);

        quantityField = new JTextField();
        quantityField.setBounds(150, 270, 200, 25);
        add(quantityField);

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setBounds(30, 310, 100, 25);
        add(locationLabel);

        locationField = new JTextField();
        locationField.setBounds(150, 310, 200, 25);
        add(locationField);

        JButton updateButton = new JButton("Update Book");
        updateButton.setBounds(150, 350, 200, 30);
        add(updateButton);

        updateButton.addActionListener(this::updateBook);

        loadBookDetails();
        setVisible(true);
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
