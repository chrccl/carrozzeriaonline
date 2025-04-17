package it.chrccl.carrozzeriaonline.components;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import it.chrccl.carrozzeriaonline.model.dao.RepairCenter;
import it.chrccl.carrozzeriaonline.model.dao.User;
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
    private String MESSAGING_SID;

    @Value("${twilio.repair.center.template.sid}")
    private String REPAIR_CENTER_TEMPLATE_SID;

    @Value("${twilio.confmsg.nobouncing.sid}")
    private String CONFMG_NOBOUNCING_SID;

    @Value("${twilio.confmsg.withbouncing.sid}")
    private String CONFMG_WITHBOUNCING_SID;

    @Value("${twilio.deleted.task.msg.sid}")
    private String DELETED_TASK_SID;

    @Value("${twilio.web.msg.sid}")
    private String WEB_MSG_SID;

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public String getUserCredentials(){
        return ACCOUNT_SID + ":" + AUTH_TOKEN;
    }

    public void sendMessage(PhoneNumber to, String body) {
        Message msg = Message.creator(to, MESSAGING_SID, body).create();
        log.info(msg.getSid());
    }

    public void sendWebMessage(PhoneNumber to) {
        Message.creator(to, WEB_MSG_SID, "").setContentSid(WEB_MSG_SID).create();
    }

    public void sendRepairCentersProposalMessage(PhoneNumber to, List<RepairCenter> repairCenters) {
        JSONObject contentVariables = new JSONObject();
        for (int i = 0; i < 3; i++) {
            RepairCenter center = repairCenters.get(i);
            log.info(center.getCompanyName());
            String name = formatRepairCenterName(center);
            contentVariables.put("carrozzeria" + (i + 1), name);
        }

        Message.creator(to, MESSAGING_SID, "")
                .setContentVariables(contentVariables.toString())
                .setContentSid(REPAIR_CENTER_TEMPLATE_SID)
                .create();
    }

    public void sendUserConfirmationMessageNoBouncing(PhoneNumber to, User user, RepairCenter repairCenter) {
        buildVariablesForConfMsg(to, user, repairCenter, CONFMG_NOBOUNCING_SID);
    }

    public void sendUserConfirmationMessageWithBouncing(PhoneNumber to, User user, RepairCenter repairCenter) {
        buildVariablesForConfMsg(to, user, repairCenter, CONFMG_WITHBOUNCING_SID);
    }

    public void sendUserDeletedTaskNotification(PhoneNumber to, User user) {
        JSONObject contentVariables = new JSONObject();
        contentVariables.put("nome_utente", user.getFullName().substring(0, user.getFullName().indexOf(' ')));
        Message.creator(to, MESSAGING_SID, "")
                .setContentVariables(contentVariables.toString())
                .setContentSid(DELETED_TASK_SID)
                .create();
    }

    public void sendMediaMessage(PhoneNumber to, String body) {
        Message msg = Message.creator(to, MESSAGING_SID, "").setMediaUrl(body).create();
        log.info(msg.getSid());
    }

    public Message sendMediaMessages(PhoneNumber to, List<URI> body) {
        Message msg = Message.creator(to, MESSAGING_SID, "").setMediaUrl(body).create();
        log.info(msg.getSid());
        return msg;
    }


    private void buildVariablesForConfMsg(PhoneNumber to, User user, RepairCenter repairCenter, String confmgWithbouncingSid) {
        JSONObject contentVariables = new JSONObject();
        contentVariables.put("nome_utente", user.getFullName().substring(0, user.getFullName().indexOf(' ')));
        contentVariables.put("nome_carrozzeria", formatRepairCenterName(repairCenter));
        contentVariables.put("telefono_carrozzeria", repairCenter.getPhoneNumber());

        Message.creator(to, MESSAGING_SID, "")
                .setContentVariables(contentVariables.toString())
                .setContentSid(confmgWithbouncingSid)
                .create();
    }

    private String formatRepairCenterName(RepairCenter center) {
        String companyName = center.getCompanyName();
        return (companyName != null && companyName.length() > 19)
                ? companyName.substring(0, 20)
                : center.getCompanyName();
    }

}
