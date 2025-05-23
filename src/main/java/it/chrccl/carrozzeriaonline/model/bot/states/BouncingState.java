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
import it.chrccl.carrozzeriaonline.services.RepairCenterService;
import it.chrccl.carrozzeriaonline.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class BouncingState implements BotState {

    private final BRCPerTaskService brcTaskService;

    private final TaskService taskService;

    private final RepairCenterService repairCenterService;

    private final EmailComponent emailComponent;

    private final AttachmentService attachmentService;

    private final TwilioComponent twilioComponent;

    @Autowired
    public BouncingState(BRCPerTaskService brcTaskService, TaskService taskService, EmailComponent emailComponent,
                         RepairCenterService repairCenterService, AttachmentService attachmentService,
                         TwilioComponent twilioComponent) {
        this.brcTaskService = brcTaskService;
        this.taskService = taskService;
        this.repairCenterService = repairCenterService;
        this.emailComponent = emailComponent;
        this.attachmentService = attachmentService;
        this.twilioComponent = twilioComponent;
    }

    @Override   //used for accept task case
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        List<BRCPerTask> brcsPerTask = brcTaskService.findByTask(context.getTask());
        List<RepairCenter> bouncedRepairCenters = brcsPerTask.stream()
                .map(brc -> brc.getBRCPerTaskId().getRepairCenter())
                .toList();
        RepairCenter repairCenter = bouncedRepairCenters.stream()
                .filter(rc -> rc.getCompanyName().contains(data.getMessageBody()))
                .findFirst().orElse(null);

        brcsPerTask.stream()
                .filter(brc -> brc.getBRCPerTaskId().getRepairCenter().equals(repairCenter))
                .findFirst()
                .ifPresent(brc -> {
                    brc.setAccepted(true);
                    brcTaskService.save(brc);
                });

        context.getTask().setAccepted(true);
        context.getTask().setStatus(TaskStatus.ACCEPTED);
        taskService.save(context.getTask());

        PhoneNumber to = new PhoneNumber(fromNumber);
        if(bouncedRepairCenters.size() == 1){
            twilioComponent.sendUserConfirmationMessageNoBouncing(to, context.getTask().getUser(), repairCenter);
        }else{
            twilioComponent.sendUserConfirmationMessageWithBouncing(to, context.getTask().getUser(), repairCenter);
        }
    }

    @Override
    public Boolean verifyMessage(Task task,  MessageData data) {
        return false;
    }

    @Override   //Used for refuse task case and rebounce the task
    public void handleError(BotContext context, String fromNumber, MessageData data) {
        List<BRCPerTask> brcsPerTask = brcTaskService.findByTask(context.getTask());
        List<RepairCenter> bouncedRepairCenters = brcsPerTask.stream()
                .map(brc -> brc.getBRCPerTaskId().getRepairCenter())
                .collect(Collectors.toList());

        if(isLatestBouncedRCEqualsToTheRefuser(brcsPerTask, data.getMessageBody())) {
            Partner partner = bouncedRepairCenters.get(0).getPartner();
            RepairCenter closestRepairCenter;
            List<RepairCenter> repairCentersToAvoid;
            if (partner == Partner.CARLINK){
                repairCentersToAvoid = Stream
                        .of(bouncedRepairCenters, repairCenterService.findRepairCentersByPartner(Partner.SAVOIA))
                        .flatMap(List::stream).collect(Collectors.toList());
            }else{
                repairCentersToAvoid = Stream
                        .of(bouncedRepairCenters, repairCenterService.findRepairCentersByPartner(Partner.CARLINK))
                        .flatMap(List::stream).collect(Collectors.toList());
            }
            closestRepairCenter = repairCenterService.findClosestRepairCentersByCap(
                    data.getMessageBody(), repairCentersToAvoid
            ).get(0);
            brcTaskService.save(new BRCPerTask(new BRCPerTaskId(context.getTask(), closestRepairCenter), LocalDateTime.now(), false));

            List<Attachment> attachments = attachmentService.findAttachmentsByTask(context.getTask());
            sendTaskToChosenRepairCenter(context, attachments, closestRepairCenter);

            context.getTask().setAccepted(false);
            context.getTask().setStatus(TaskStatus.BOUNCING);
            taskService.save(context.getTask());
        }
    }

    private Boolean isLatestBouncedRCEqualsToTheRefuser(List<BRCPerTask> bcrTasks, String companyName) {
        return bcrTasks.stream()
                .max(Comparator.comparing(BRCPerTask::getAssignedAt))
                .map(b -> b.getBRCPerTaskId().getRepairCenter())
                .map(rc -> rc.getCompanyName().contains(companyName))
                .orElse(false);
    }

    private void sendTaskToChosenRepairCenter(BotContext context, List<Attachment> attachments, RepairCenter rc) {
        switch (rc.getPartner()){
            case CARLINK, INTERNAL -> sendTaskCarlinkRepairCenter(context, attachments, rc);
            case SAVOIA -> sendTaskSavoiaRepairCenter(context, attachments, rc);
        }
    }

    private void sendTaskCarlinkRepairCenter(BotContext context, List<Attachment> attachments, RepairCenter rc) {
        Map<String, Object> variables = sendCarlinkCommunication(context, attachments, rc);
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

    private void sendTaskSavoiaRepairCenter(BotContext context, List<Attachment> attachments, RepairCenter rc) {
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
                                                         RepairCenter rc) {
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
