package librarysystem;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class NotifyReservation {
    public static void sendEmail(String to, String bookTitle) {
        String from = "library.systemm21@gmail.com"; 
        String password = "jwqv iyty ebud xszb"; // App Password

        Properties properties = System.getProperties(); // Use system properties like SendEmail
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.debug", "true"); // Enable debugging

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Library Reservation Notification");
            message.setText("Dear User,\n\nYour reserved book '" + bookTitle + "' is now available for pickup.\n\nThank you.");

            Transport.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
