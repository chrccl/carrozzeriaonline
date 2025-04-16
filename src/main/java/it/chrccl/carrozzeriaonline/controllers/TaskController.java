package it.chrccl.carrozzeriaonline.controllers;

import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.BotStatesFactory;
import it.chrccl.carrozzeriaonline.bot.MessageData;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.model.dao.TaskId;
import it.chrccl.carrozzeriaonline.model.dao.TaskStatus;
import it.chrccl.carrozzeriaonline.model.dao.User;
import it.chrccl.carrozzeriaonline.services.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class TaskController {

    private final TaskService taskService;

    private final BotStatesFactory botStatesFactory;

    @Autowired
    public TaskController(final TaskService taskService, final BotStatesFactory botStatesFactory) {
        this.taskService = taskService;
        this.botStatesFactory = botStatesFactory;
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
            if(numMedia > 0 && task.getStatus() != TaskStatus.MULTIMEDIA){
                botContext = new BotContext(botStatesFactory.getMultimediaState(), task);
                botContext.handle(fromNumber, messageData);
            }
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
    public ResponseEntity<String> handleWebPlatformIncomingTask(@RequestBody Task task){
        //TODO
        return null;
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
        //TODO
    }
    
}
