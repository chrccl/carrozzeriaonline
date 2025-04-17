package it.chrccl.carrozzeriaonline.components;

import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.ThymeleafVariables;
import it.chrccl.carrozzeriaonline.model.dao.Attachment;
import it.chrccl.carrozzeriaonline.model.dao.Partner;
import it.chrccl.carrozzeriaonline.model.dao.RepairCenter;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import jakarta.activation.DataHandler;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class EmailComponent {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    public EmailComponent(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendTaskNotification(String to, String subject, Map<String, Object> variables,
                                      List<Attachment> fileToAttach, String template) {
        log.info("Preparing email to: {} with subject: {}", to, subject);
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setFrom(new InternetAddress("info@assistenza.carrozzeriaonline.com"));
            mimeMessage.setSubject(subject);
            log.debug("Setting up Thymeleaf context with variables: {}", variables.keySet());

            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(template, context);
            MimeMultipart multipart = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            if (fileToAttach != null && !fileToAttach.isEmpty()) {
                log.info("Adding {} attachments to the email", fileToAttach.size());
                for (Attachment attachment : fileToAttach) {
                    log.debug("Attaching file: {} (type: {})", attachment.getName(), attachment.getContentType());
                    byte[] attachmentBytes = Base64.getDecoder().decode(attachment.getBase64Data());
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.setDataHandler(new DataHandler(
                            new ByteArrayDataSource(attachmentBytes, attachment.getContentType())));
                    attachmentPart.setFileName(attachment.getName());
                    multipart.addBodyPart(attachmentPart);
                }
            }
            mimeMessage.setContent(multipart);
        };

        try {
            mailSender.send(preparator);
            log.info("Email sent successfully to: {}", to);
        } catch (MailException ex) {
            log.error("Failed to send email to: {} - {}", to, ex.getMessage(), ex);
        }
    }

    public Map<String, Object> buildThymeleafVariables(Task task, RepairCenter rc, Boolean isCarlink){
        Map<String, Object> variables = new HashMap<>();
        variables.put(ThymeleafVariables.REPAIR_CENTER_NAME_PLACEHOLDER, rc.getCompanyName());
        variables.put(ThymeleafVariables.REPAIR_CENTER_EMAIL_PLACEHOLDER, rc.getEmail());
        variables.put(ThymeleafVariables.REPAIR_CENTER_PHONE_PLACEHOLDER, rc.getPhoneNumber());
        variables.put(ThymeleafVariables.USER_FULLNAME_PLACEHOLDER, task.getUser().getFullName());
        variables.put(ThymeleafVariables.USER_PHONE_PLACEHOLDER, task.getUser().getMobilePhone());
        variables.put(ThymeleafVariables.LICENSE_PLATE_PLACEHOLDER, task.getLicensePlate());
        variables.put(ThymeleafVariables.TIMESTAMP, task.getCreatedAt());
        if(isCarlink){
            variables.put(ThymeleafVariables.PARTNER_NAME_PLACEHOLDER, Partner.CARLINK.name());
            variables.put(ThymeleafVariables.PARTNER_EMAIL_PLACEHOLDER, Constants.CARLINK_TASKS_EMAIL);
            variables.put(ThymeleafVariables.IS_CARLINK_FLAG, true);
        }else{
            variables.put(ThymeleafVariables.PARTNER_NAME_PLACEHOLDER, Partner.SAVOIA.name());
            variables.put(ThymeleafVariables.PARTNER_EMAIL_PLACEHOLDER, Constants.SAVOIA_TASKS_EMAIL);
            variables.put(ThymeleafVariables.IS_CARLINK_FLAG, false);
        }
        return variables;
    }

}
