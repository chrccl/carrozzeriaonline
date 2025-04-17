package it.chrccl.carrozzeriaonline.controllers;

import com.twilio.type.PhoneNumber;

import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.BotStatesFactory;
import it.chrccl.carrozzeriaonline.bot.MessageData;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.WebTask;
import it.chrccl.carrozzeriaonline.model.dao.*;
import it.chrccl.carrozzeriaonline.services.AttachmentService;
import it.chrccl.carrozzeriaonline.services.BRCPerTaskService;
import it.chrccl.carrozzeriaonline.services.RepairCenterService;
import it.chrccl.carrozzeriaonline.services.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class TaskController {

    private final TaskService taskService;

    private final BRCPerTaskService brcPerTaskService;

    private final BotStatesFactory botStatesFactory;

    private final TwilioComponent twilioComponent;

    private final AttachmentService attachmentService;

    private final RepairCenterService repairCenterService;

    @Autowired
    public TaskController(TaskService taskService, BRCPerTaskService brcPerTaskService,
                          BotStatesFactory botStatesFactory, TwilioComponent twilioComponent,
                          AttachmentService attachmentService, RepairCenterService repairCenterService) {
        this.taskService = taskService;
        this.brcPerTaskService = brcPerTaskService;
        this.botStatesFactory = botStatesFactory;
        this.twilioComponent = twilioComponent;
        this.attachmentService = attachmentService;
        this.repairCenterService = repairCenterService;
    }

    @PostMapping("/handleWhatsappMessage")
    public ResponseEntity<String> sendWhatsappTest(@RequestParam("From") String fromNumber,
            @RequestParam("Body") String messageBody, @RequestParam("NumMedia") Integer numMedia,
            @RequestParam(name = "MediaContentType0", required = false) String contentTypeAttachment,
            @RequestParam(name = "MediaUrl0", required = false) String mediaUrlAttachment) {
        MessageData messageData = new MessageData(messageBody, numMedia, contentTypeAttachment, mediaUrlAttachment);
        Optional<Task> optionalTask = taskService.findOngoingTaskByPhoneNumber(fromNumber);
        BotContext botContext; BotState currentBotState; Task task; Boolean errorOccurred;
        if (optionalTask.isPresent()) {
            task = optionalTask.get();
            checkOutOfOrderMedia(task, numMedia, fromNumber, messageData);
            if (task.getStatus() == TaskStatus.WEB) return handleWebTask(task, messageData, fromNumber);

            currentBotState = botStatesFactory.getStateFromTask(task);
            errorOccurred = currentBotState.verifyMessage(task, messageData);
        } else {
            task = Task.builder()
                    .id(new TaskId(fromNumber, LocalDateTime.now())).user(new User(fromNumber))
                    .status(TaskStatus.INITIAL_STATE).isWeb(false).accepted(false)
                    .build();
            errorOccurred = false;
            currentBotState = botStatesFactory.getInitialState();
        }
        botContext = new BotContext(currentBotState, task);
        if (errorOccurred) {
            botContext.handleError(fromNumber, messageData);
        }else{
            botContext.handle(fromNumber, messageData);
        }
        return ResponseEntity.ok("Message processed successfully.");
    }

    @PostMapping("handleWebPlatformIncomingTask")
    public ResponseEntity<String> handleWebPlatformIncomingTask(@RequestBody WebTask webTask){
        Task task = webTask.getTask();
        List<Attachment> attachments = webTask.getAttachments();
        String userPhone = Constants.TWILIO_PREFIX + task.getUser().getMobilePhone();
        Optional<Task> optionalTask = taskService.findOngoingTaskByPhoneNumber(userPhone);
        if(optionalTask.isPresent()) return ResponseEntity.internalServerError()
                .body("Impossibile creare un nuovo incarico, ne hai già uno in corso sulla piattaforma Whatsapp.");

        task.getUser().setMobilePhone(userPhone);
        taskService.save(task);
        attachmentService.saveAll(attachments);

        RepairCenter rc = repairCenterService.findRepairCentersByCompanyNameIsLikeIgnoreCase(webTask.getCompanyName());
        if (rc != null) {
            brcPerTaskService.save(new BRCPerTask(new BRCPerTaskId(task, rc), task.getId().getCreatedAt(), false));
            twilioComponent.sendWebMessage(new PhoneNumber(userPhone));
            return ResponseEntity.ok("Message processed successfully.");
        }else{
            return ResponseEntity.internalServerError().body("No Repair Center associated to the request");
        }
    }

    @GetMapping("/acceptIncarico/{telefono}/{ragioneSocialeCarrozzeria}/{timestamp}")
    public ResponseEntity<String> acceptIncarico(
            @PathVariable("telefono") String fromNumber,
            @PathVariable("ragioneSocialeCarrozzeria") String companyName,
            @PathVariable("timestamp")
            @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp) {
        Task task = taskService.findTaskById(new TaskId(fromNumber, timestamp));
        if(task != null && task.getStatus() == TaskStatus.BOUNCING){
            MessageData messageData = new MessageData(companyName, 0, null, null);
            new BotContext(botStatesFactory.getStateFromTask(task), task).handle(fromNumber, messageData);
        }
        return ResponseEntity.ok("Incarico accepted for " + companyName + " at " + timestamp);
    }

    @GetMapping("/refuseIncarico/{telefono}/{timestamp}")
    public ResponseEntity<String> refuseIncarico(
            @PathVariable("telefono") String fromNumber,
            @PathVariable("timestamp")
            @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") LocalDateTime timestamp) {
        Task task = taskService.findTaskById(new TaskId(fromNumber, timestamp));
        if(task != null && task.getStatus() == TaskStatus.BOUNCING){
            new BotContext(botStatesFactory.getStateFromTask(task), task).handleError(fromNumber, null);
        }
        return ResponseEntity.ok("Incarico refused for " + fromNumber + " at " + timestamp);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkTasksToBounce() {
        List<Task> tasks = taskService.findTasksByStatus(TaskStatus.BOUNCING);
        tasks.forEach(task -> {
            LocalDateTime now = LocalDateTime.now();
            if (task.getId().getCreatedAt().isBefore(now.minusDays(3))) {
                task.setStatus(TaskStatus.DELETED);
                taskService.save(task);
                twilioComponent.sendUserDeletedTaskNotification(
                        new PhoneNumber(task.getUser().getMobilePhone()),
                        task.getUser()
                );
                return;
            }
            brcPerTaskService.findByTask(task).stream()
                    .max(Comparator.comparing(BRCPerTask::getAssignedAt))
                    .filter(brc -> brc.getAssignedAt().isBefore(now.minusHours(25)))
                    .ifPresent(latest -> {
                        new BotContext(botStatesFactory.getStateFromTask(task), task)
                                .handleError(task.getUser().getMobilePhone(), null);
                    });
        });
    }

    private ResponseEntity<String> handleWebTask(Task task, MessageData messageData, String fromNumber) {
        new BotContext(botStatesFactory.getStateFromTask(task), task).handle(fromNumber, messageData);
        return ResponseEntity.ok("Message processed successfully.");
    }

    private void checkOutOfOrderMedia(Task task, Integer numMedia, String fromNumber, MessageData data) {
        if(numMedia > 0 && task.getStatus() != TaskStatus.MULTIMEDIA){
            BotContext botContext = new BotContext(botStatesFactory.getMultimediaState(), task);
            botContext.handle(fromNumber, data);
        }
    }
    
}
