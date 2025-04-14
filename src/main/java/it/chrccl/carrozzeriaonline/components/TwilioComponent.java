package it.chrccl.carrozzeriaonline.components;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class TwilioComponent {

    @Value("${twilio.account.sid}")
    private static String ACCOUNT_SID;

    @Value("${twilio.auth.token}")
    private static String AUTH_TOKEN;

    @Value("${twilio.messaging.sid}")
    private static String MESSAGING_SID;

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public String getUserCredentials(){
        return ACCOUNT_SID + ":" + AUTH_TOKEN;
    }

    public Message sendMessage(PhoneNumber to, String body) {
        Message msg = Message.creator(to, MESSAGING_SID, body).create();
        log.info(msg.getSid());
        return msg;
    }

    public Message sendMediaMessage(PhoneNumber to, URI body) {
        Message msg = Message.creator(to, MESSAGING_SID, "").setMediaUrl(body).create();
        log.info(msg.getSid());
        return msg;
    }

    public Message sendMediaMessages(PhoneNumber to, List<URI> body) {
        Message msg = Message.creator(to, MESSAGING_SID, "").setMediaUrl(body).create();
        log.info(msg.getSid());
        return msg;
    }

}
