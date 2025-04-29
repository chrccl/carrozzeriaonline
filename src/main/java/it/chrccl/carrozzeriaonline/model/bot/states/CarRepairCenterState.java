package it.chrccl.carrozzeriaonline.model.bot.states;

import com.twilio.type.PhoneNumber;
import it.chrccl.carrozzeriaonline.components.EmailComponent;
import it.chrccl.carrozzeriaonline.components.IOComponent;
import it.chrccl.carrozzeriaonline.components.ImgBBComponent;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.ThymeleafVariables;
import it.chrccl.carrozzeriaonline.model.bot.BotContext;
import it.chrccl.carrozzeriaonline.model.bot.BotState;
import it.chrccl.carrozzeriaonline.model.bot.MessageData;
import it.chrccl.carrozzeriaonline.model.dao.*;
import it.chrccl.carrozzeriaonline.services.AttachmentService;
import it.chrccl.carrozzeriaonline.services.BRCPerTaskService;
import it.chrccl.carrozzeriaonline.services.RepairCenterService;
import it.chrccl.carrozzeriaonline.services.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CarRepairCenterState implements BotState {

    private final TwilioComponent twilio;

    private final TaskService taskService;

    private final RepairCenterService repairCenterService;

    private final AttachmentService attachmentService;

    private final EmailComponent emailComponent;

    private final ImgBBComponent imgBBComponent;

    private final IOComponent ioComponent;

    private final BRCPerTaskService brcPerTaskService;

    @Autowired
    public CarRepairCenterState(TwilioComponent twilio, TaskService taskService, RepairCenterService repairCenterService,
                                AttachmentService attachmentService, EmailComponent emailComponent,
                                ImgBBComponent imgBBComponent, IOComponent ioComponent,
                                BRCPerTaskService brcPerTaskService) {
        this.twilio = twilio;
        this.taskService = taskService;
        this.repairCenterService = repairCenterService;
        this.attachmentService = attachmentService;
        this.emailComponent = emailComponent;
        this.imgBBComponent = imgBBComponent;
        this.ioComponent = ioComponent;
        this.brcPerTaskService = brcPerTaskService;
    }

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        PhoneNumber to = new PhoneNumber(fromNumber);
        String[] contactsOfCustomRepairCenter = extractContact(data.getMessageBody());
        List<Attachment> attachments = attachmentService.findAttachmentsByTask(context.getTask());
        RepairCenter rc;
        if (contactsOfCustomRepairCenter != null) {
            rc = repairCenterService.findClosestRepairCentersByCap(
                    context.getTask().getUser().getPreferredCap(),
                    null
            ).get(0);
            sendTaskToCustomRepairCenter(
                    context, attachments, fromNumber,
                    contactsOfCustomRepairCenter[0], contactsOfCustomRepairCenter[1], rc
            );
            twilio.sendMessage(to, Constants.BOT_CUSTOM_REPAIR_CENTER_CHOSEN_MESSAGE);
        } else {
            rc = repairCenterService.findRepairCentersByCompanyNameIsLikeIgnoreCase(data.getMessageBody());
            sendTaskToChosenRepairCenter(context, attachments, fromNumber, rc);
        }
        brcPerTaskService.save(new BRCPerTask(new BRCPerTaskId(context.getTask(), rc),  LocalDateTime.now(), false));

        context.getTask().setStatus(TaskStatus.BOUNCING);
        taskService.save(context.getTask());
    }

    @Override
    public Boolean verifyMessage(Task task, MessageData data) {
        if (extractContact(data.getMessageBody()) != null) {
            return false;
        } else return repairCenterService.findRepairCentersByCompanyNameIsLikeIgnoreCase(data.getMessageBody()) == null;
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {
        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(to, Constants.BOT_REPAIR_CENTER_NOT_KNOWN_MESSAGE);
    }

    private void sendTaskToCustomRepairCenter(BotContext context, List<Attachment> attachments, String fromNumber,
                                              String email, String phoneNumber, RepairCenter rc) {
        Map<String, Object> variables = sendCarlinkCommunication(context, attachments, fromNumber, rc);
        variables.put(ThymeleafVariables.REPAIR_CENTER_NAME_PLACEHOLDER, Constants.COMPANY_NAME_NOT_PROVIDED);
        variables.put(ThymeleafVariables.REPAIR_CENTER_EMAIL_PLACEHOLDER, email);
        variables.put(ThymeleafVariables.REPAIR_CENTER_PHONE_PLACEHOLDER, phoneNumber);
        emailComponent.sendTaskNotification(
                email,
                String.format(Constants.TASK_EMAIL_SUBJECT, context.getTask().getLicensePlate()),
                variables,
                attachments,
                Constants.TEMPLATE_REPAIR_CENTER_TASK_EMAIL
        );
    }

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
        try{
            PhoneNumber to = new PhoneNumber(fromNumber);
            twilio.sendMessage(
                    to,
                    String.format(Constants.BOT_SAVOIA_REPAIR_CENTER_CHOSEN_MESSAGE, rc.getCompanyName(),
                            rc.getAddress(), rc.getCity(), rc.getPhoneNumber())
            );
            Path warrantPath = Path.of(String.format(
                    Constants.USER_SAVOIA_WARRANT_PATH_FORMAT,
                    context.getTask().getUser().getMobilePhone() + "_" +
                            context.getTask().getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
            );
            String url = imgBBComponent.uploadImage(
                    warrantPath.getParent().toString(), warrantPath.getFileName().toString()
            );
            attachments.add(saveWarrantAsAttachment(context, warrantPath, url));
            twilio.sendMediaMessage(to, URI.create(url));

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
        }catch (IOException ignored){ }
    }

    private Map<String, Object> sendCarlinkCommunication(BotContext context, List<Attachment> attachments,
                                                         String fromNumber, RepairCenter rc) {
        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(
                to,
                String.format(Constants.BOT_CARLINK_REPAIR_CENTER_CHOSEN_MESSAGE, rc.getCompanyName(),
                        rc.getAddress(), rc.getCity())
        );
        Path warrantPath = Path.of(
                String.format(Constants.USER_CARLINK_WARRANT_PATH_FORMAT,
                        context.getTask().getUser().getMobilePhone() + "_" +
                                context.getTask().getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
        );
        try {
            String url = imgBBComponent.uploadImage(
                    warrantPath.getParent().toString(), warrantPath.getFileName().toString()
            );
            attachments.add(saveWarrantAsAttachment(context, warrantPath, url));
            twilio.sendMediaMessage(to, URI.create(url));
        }catch (IOException ignored){ }

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

    private Attachment saveWarrantAsAttachment(BotContext context, Path warrantPath, String url) throws IOException {
        Attachment warrantAttachment = Attachment.builder()
                .name(warrantPath.getFileName().toString())
                .contentType(Constants.WARRANT_CONTENT_TYPE)
                .filePath(warrantPath.toString())
                .url(url)
                .task(context.getTask())
                .build();
        context.getTask().setWarrantUrl(url);
        return attachmentService.save(warrantAttachment);
    }

    private String[] extractContact(String message) {
        if (message == null || message.isEmpty()) {
            return null;
        }
        String emailRegex = "[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}";
        String phoneRegex = "((\\+\\d{1,3}[ ]?)?(\\d{9}|\\d{10}))";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Pattern phonePattern = Pattern.compile(phoneRegex);
        Matcher emailMatcher = emailPattern.matcher(message);
        Matcher phoneMatcher = phonePattern.matcher(message);
        if (!emailMatcher.find()) {
            return null;
        }
        String email = emailMatcher.group();
        if (!phoneMatcher.find()) {
            return null;
        }
        String phone = phoneMatcher.group();

        return new String[]{ email, phone };
    }

}
