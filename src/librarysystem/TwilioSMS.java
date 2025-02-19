package librarysystem;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSMS {
    // Retrieve Twilio credentials from environment variables
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");

    static {
        if (ACCOUNT_SID == null || ACCOUNT_SID.isEmpty() || AUTH_TOKEN == null || AUTH_TOKEN.isEmpty()) {
            throw new IllegalStateException("Twilio credentials are missing! Make sure environment variables are set.");
        }
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void sendSMS(String userPhone, String bookTitle) {
        try {
            String messageBody = "Reminder: The book '" + bookTitle + "' is overdue. Please return it soon.";

            Message message = Message.creator(
                    new PhoneNumber(userPhone),
                    new PhoneNumber(System.getenv("TWILIO_PHONE_NUMBER")),
                    messageBody
            ).create();

            System.out.println("Message SID: " + message.getSid());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send SMS. Please check your Twilio setup.");
        }
    }
}
