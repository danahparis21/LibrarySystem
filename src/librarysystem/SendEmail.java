package librarysystem;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SendEmail {
    // Method to send email notification
    public static void sendEmail(String to, String bookTitle) {
        String from = "library.systemm21@gmail.com"; // Your Gmail address
        String host = "smtp.gmail.com"; // Gmail's SMTP server

        // Set system properties for the Gmail SMTP server
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587"); // 587 is the default for TLS
        properties.put("mail.smtp.starttls.enable", "true"); // Enable TLS
        properties.put("mail.smtp.auth", "true");

        // Create a session with the required credentials
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("library.systemm21@gmail.com", "jwqv iyty ebud xszb");
            }
        });

        try {
            // Create a message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from)); // Set from address
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); // Set recipient
            message.setSubject("Overdue Book Notification"); // Set subject
            message.setText("Dear User, \n\nThis is a reminder that the book '" + bookTitle + "' is overdue. Please return it at your earliest convenience.\n\nThank you."); // Set message body

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully.");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
