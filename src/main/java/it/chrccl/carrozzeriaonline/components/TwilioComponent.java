package it.chrccl.carrozzeriaonline.components;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import it.chrccl.carrozzeriaonline.model.dao.RepairCenter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
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

    @Value("${twilio.repair.center.template.sid}")
    private static String REPAIR_CENTER_TEMPLATE_SID;

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

    public Message sendRepairCentersProposalMessage(PhoneNumber to, List<RepairCenter> repairCenters) {
        JSONObject contentVariables = new JSONObject();
        for (int i = 0; i < 3; i++) {
            RepairCenter center = repairCenters.get(i);
            log.info(center.getCompanyName());
            String name = formatRepairCenterName(center);
            contentVariables.put("carrozzeria" + (i + 1), name);
        }

        return Message.creator(to, MESSAGING_SID, "")
                .setContentVariables(contentVariables.toString())
                .setContentSid(REPAIR_CENTER_TEMPLATE_SID)
                .create();
    }

    private String formatRepairCenterName(RepairCenter center) {
        String companyName = center.getCompanyName();
        return (companyName != null && companyName.length() > 19)
                ? companyName.substring(0, 20)
                : center.getCompanyName();
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
