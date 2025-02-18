package librarysystem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class addBook extends JFrame {
    private JTextField titleField, authorField, isbnField, genreField, publisherField, yearField, quantityField, locationField;

    public addBook() {
        setTitle("Add Book");
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

        JButton addButton = new JButton("Add Book");
        addButton.setBounds(150, 350, 200, 30);
        add(addButton);

        addButton.addActionListener(this::addBook);

        setVisible(true);
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
