package it.chrccl.carrozzeriaonline.bot.states;

import com.twilio.type.PhoneNumber;

import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.MessageData;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.dao.Attachment;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.services.AttachmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
public class MultimediaState implements BotState {

    private final TwilioComponent twilio;

    private final AttachmentService attachmentService;

    @Autowired
    public MultimediaState(TwilioComponent twilio, AttachmentService attachmentService) {
        this.twilio = twilio;
        this.attachmentService = attachmentService;
    }

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        Attachment attachment = getAttachment(context, data);
        if (attachment != null) attachmentService.save(attachment);

        if(checkMinimumAttachmentsPerTask(context.getTask())){
            PhoneNumber to = new PhoneNumber(fromNumber);
            twilio.sendMessage(to, Constants.BOT_DATE_MESSAGE);
        }
    }

    @Override
    public Boolean verifyMessage(Task task, MessageData data) {
        return !checkMinimumAttachmentsPerTask(task);
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {
        handleMessage(context, fromNumber, data);
    }

    private Attachment getAttachment(BotContext context, MessageData data) {
        try {
            URL url = new URL(data.getMediaUrlAttachment());
            URLConnection connection = url.openConnection();
            String userCredentials = twilio.getUserCredentials();
            connection.setRequestProperty("Authorization",
                    "Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes()));

            InputStream inputStream = connection.getInputStream();
            byte[] imageBytes = inputStream.readAllBytes();
            String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

            return Attachment.builder()
                    .name("attachment_" + UUID.randomUUID())
                    .contentType(data.getContentTypeAttachment())
                    .base64Data(imageBase64)
                    .url(data.getMediaUrlAttachment())
                    .task(context.getTask())
                    .build();
        } catch (IOException e) {
            return null;
        }
    }

    private Boolean checkMinimumAttachmentsPerTask(Task task) {
        List<Attachment> attachments = attachmentService.findAttachmentsByTask(task);
        return attachments.size() == 3 ||
                attachments.stream().anyMatch(attachment -> attachment.getContentType().contains("pdf"));
    }

}
