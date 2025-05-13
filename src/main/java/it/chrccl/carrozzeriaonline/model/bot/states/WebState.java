package it.chrccl.carrozzeriaonline.model.bot.states;

import com.twilio.type.PhoneNumber;
import it.chrccl.carrozzeriaonline.components.EmailComponent;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.bot.BotContext;
import it.chrccl.carrozzeriaonline.model.bot.BotState;
import it.chrccl.carrozzeriaonline.model.bot.MessageData;
import it.chrccl.carrozzeriaonline.model.entities.*;
import it.chrccl.carrozzeriaonline.services.AttachmentService;
import it.chrccl.carrozzeriaonline.services.BRCPerTaskService;
import it.chrccl.carrozzeriaonline.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class WebState implements BotState {

    private final TwilioComponent twilio;

    private final TaskService taskService;

    private final AttachmentService attachmentService;

    private final EmailComponent emailComponent;

    private final BRCPerTaskService brcPerTaskService;

    @Autowired
    public WebState(TwilioComponent twilio, TaskService taskService, BRCPerTaskService brcPerTaskService,
                                AttachmentService attachmentService, EmailComponent emailComponent) {
        this.twilio = twilio;
        this.taskService = taskService;
        this.attachmentService = attachmentService;
        this.emailComponent = emailComponent;
        this.brcPerTaskService = brcPerTaskService;
    }

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        List<Attachment> attachments = attachmentService.findAttachmentsByTask(context.getTask());
        brcPerTaskService.findByTask(context.getTask()).stream().findFirst().ifPresent(brc -> {
            sendTaskToChosenRepairCenter(context, attachments, fromNumber, brc.getBRCPerTaskId().getRepairCenter());
        });

        context.getTask().setStatus(TaskStatus.BOUNCING);
        taskService.save(context.getTask());
    }

    @Override
    public Boolean verifyMessage(Task task, MessageData data) {
        return null;
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) { }

    private void sendTaskToChosenRepairCenter(BotContext context, List<Attachment> attachments, String fromNumber,
                                              RepairCenter rc) {
        switch (rc.getPartner()){
            case CARLINK, INTERNAL -> sendTaskCarlinkRepairCenter(context, attachments, fromNumber, rc);
            case SAVOIA -> sendTaskSavoiaRepairCenter(context, attachments, fromNumber, rc);
        }
    }

    private void sendTaskCarlinkRepairCenter(BotContext context, List<Attachment> attachments, String fromNumber,
                                             RepairCenter rc) {
        Map<String, Object> variables = sendCarlinkCommunication(context, attachments, fromNumber, rc);
        if(rc.getPartner() == Partner.INTERNAL){
            emailComponent.sendTaskNotification(
                    rc.getEmail(),
                    String.format(Constants.TASK_EMAIL_SUBJECT, context.getTask().getLicensePlate()),
                    variables,
                    attachments,
                    Constants.TEMPLATE_REPAIR_CENTER_TASK_EMAIL
            );
        }
    }

    private void sendTaskSavoiaRepairCenter(BotContext context, List<Attachment> attachments, String fromNumber,
                                            RepairCenter rc) {
        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(
                to,
                String.format(Constants.BOT_SAVOIA_REPAIR_CENTER_CHOSEN_MESSAGE, rc.getCompanyName(),
                        rc.getAddress(), rc.getCity(), rc.getPhoneNumber())
        );
        twilio.sendMediaMessages(
                to,
                attachments.stream()
                        .map(Attachment::getUrl)
                        .map(URI::create)
                        .collect(Collectors.toList())
        );

        Map<String, Object> variables = emailComponent.buildThymeleafVariables(context.getTask(), rc, false);
        emailComponent.sendTaskNotification(
                Constants.SAVOIA_TASKS_EMAIL,
                String.format(Constants.TASK_EMAIL_SUBJECT, context.getTask().getLicensePlate()),
                variables,
                attachments,
                Constants.TEMPLATE_PARTNER_TASK_EMAIL
        );
        emailComponent.sendTaskNotification(
                rc.getEmail(),
                String.format(Constants.TASK_EMAIL_SUBJECT, context.getTask().getLicensePlate()),
                variables,
                attachments,
                Constants.TEMPLATE_REPAIR_CENTER_TASK_EMAIL
        );
    }

    private Map<String, Object> sendCarlinkCommunication(BotContext context, List<Attachment> attachments,
                                                         String fromNumber, RepairCenter rc) {
        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(
                to,
                String.format(Constants.BOT_CARLINK_REPAIR_CENTER_CHOSEN_MESSAGE, rc.getCompanyName(),
                        rc.getAddress(), rc.getCity())
        );
        twilio.sendMediaMessages(
                to,
                attachments.stream()
                        .map(Attachment::getUrl)
                        .map(URI::create)
                        .collect(Collectors.toList())
        );

        Map<String, Object> variables = emailComponent.buildThymeleafVariables(context.getTask(), rc,true);
        emailComponent.sendTaskNotification(
                Constants.CARLINK_TASKS_EMAIL,
                String.format(Constants.TASK_EMAIL_SUBJECT, context.getTask().getLicensePlate()),
                variables,
                attachments,
                Constants.TEMPLATE_PARTNER_TASK_EMAIL
        );
        return variables;
    }
}
