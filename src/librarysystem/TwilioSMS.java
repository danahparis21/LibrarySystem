package librarysystem;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSMS {
    // Your Twilio Account SID and Auth Token
    public static final String ACCOUNT_SID = "ACf7e83163cc2fa545aaae307853e6244b";
    public static final String AUTH_TOKEN = "9cbddd8644370ae0afe516a50b68ec2e";
    
    // Initialize Twilio (can be done once during the class initialization)
    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    // Method to send SMS
    public static void sendSMS(String userPhone, String bookTitle) {
        // Send an SMS
        String messageBody = "Reminder: The book '" + bookTitle + "' is overdue. Please return it soon.";

        Message message = Message.creator(
                new PhoneNumber(userPhone), // To number (using the passed userPhone)
                new PhoneNumber("+13252464614"), // From number (Twilio number)
                messageBody) // Message body
            .create();

        System.out.println("Message SID: " + message.getSid());
    }

    public static void main(String[] args) {
        // Test sendSMS (optional, you can comment this out if not needed)
       // sendSMS("+639605574527", "Test Book Title");
    }
}
