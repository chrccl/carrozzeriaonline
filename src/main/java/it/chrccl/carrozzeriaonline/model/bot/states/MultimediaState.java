package it.chrccl.carrozzeriaonline.model.bot.states;

import com.twilio.type.PhoneNumber;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.bot.BotContext;
import it.chrccl.carrozzeriaonline.model.bot.BotState;
import it.chrccl.carrozzeriaonline.model.bot.MessageData;
import it.chrccl.carrozzeriaonline.model.dao.Attachment;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.model.dao.TaskStatus;
import it.chrccl.carrozzeriaonline.services.AttachmentService;
import it.chrccl.carrozzeriaonline.services.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MultimediaState implements BotState {

    private final TwilioComponent twilio;

    private final AttachmentService attachmentService;

    private final TaskService taskService;

    @Autowired
    public MultimediaState(TwilioComponent twilio, AttachmentService attachmentService, TaskService taskService) {
        this.twilio = twilio;
        this.attachmentService = attachmentService;
        this.taskService = taskService;
    }

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        saveAttachment(context, data);

        List<Attachment> attachments = attachmentService.findAttachmentsByTask(context.getTask());
        PhoneNumber to = new PhoneNumber(fromNumber);
        if(context.getTask().getStatus() == TaskStatus.MULTIMEDIA
                && (attachments.size() == Constants.MIN_ATTACHMENTS_PER_TASK
                    || attachments.stream().anyMatch(att -> att.getContentType().contains("pdf")))) {
            twilio.sendMessage(to, Constants.BOT_DATE_MESSAGE);

            context.getTask().setStatus(TaskStatus.DATE);
            taskService.save(context.getTask());
        } else if (checkMinimumAttachmentsPerTask(context.getTask(), attachments)
                    && context.getTask().getStatus() != TaskStatus.MULTIMEDIA) {
            twilio.sendMessage(to, Constants.BOT_OUT_OF_ORDER_ATTACHMENT);
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

    private void saveAttachment(BotContext context, MessageData data) {
        Attachment attachment = attachmentService.createFromUrl(context, data);
        if(attachment != null){
            log.info("Attachment saved: {}", attachment.getId());
        }else {
            log.error("Attachment not saved");
        }
    }

    private Boolean checkMinimumAttachmentsPerTask(Task task) {
        List<Attachment> attachments = attachmentService.findAttachmentsByTask(task);
        return attachments.size() >= 3 ||
                attachments.stream().anyMatch(attachment -> attachment.getContentType().contains("pdf"));
    }

    private Boolean checkMinimumAttachmentsPerTask(Task task, List<Attachment> attachments) {
        return attachments.size() >= 3 ||
                attachments.stream().anyMatch(attachment -> attachment.getContentType().contains("pdf"));
    }

}
