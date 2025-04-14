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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * Endpoint to receive incoming WhatsApp messages.
     *
     * @param fromNumber          the sender's phone number.
     * @param messageBody         the message text.
     * @param numMedia            the number of media attachments.
     * @param contentTypeAttachment optional content type of the first media.
     * @param mediaUrlAttachment    optional URL of the first media.
     *
     * @return a ResponseEntity with the processing status.
     *
     */
    @PostMapping("/handleWhatsappMessage")
    public ResponseEntity<String> sendWhatsappTest(@RequestParam("From") String fromNumber,
            @RequestParam("Body") String messageBody, @RequestParam("NumMedia") Integer numMedia,
            @RequestParam(name = "MediaContentType0", required = false) String contentTypeAttachment,
            @RequestParam(name = "MediaUrl0", required = false) String mediaUrlAttachment) {
        MessageData messageData = new MessageData(messageBody, numMedia, contentTypeAttachment, mediaUrlAttachment);
        Optional<Task> optionalTask = taskService.findOngoingTaskByPhoneNumber(fromNumber);
        BotContext botContext;
        BotState currentBotState;
        Task task;
        Boolean errorOccurred;
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
    
}
