package librarysystem;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSMS {
    // Your Twilio Account SID and Auth Token
    public static final String ACCOUNT_SID = "ACf7e83163cc2fa545aaae307853e6244b";
    public static final String AUTH_TOKEN = "9cbddd8644370ae0afe516a50b68ec2e";
    
    public static void main(String[] args) {
        // Initialize Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        
        // Send an SMS
        Message message = Message.creator(
                new PhoneNumber("+639605574527"), // To number
                new PhoneNumber("+13252464614"), // From number
                "Hello IM DANAH YAY!") // Message body
            .create();
        
        System.out.println("Message SID: " + message.getSid());
    }
}
