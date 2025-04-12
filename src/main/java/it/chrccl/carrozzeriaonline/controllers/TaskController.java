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
     * @return a ResponseEntity with the processing status.
     *
     */
    @PostMapping("/handleWhatsappMessage")
    public ResponseEntity<String> sendWhatsappTest(
            @RequestParam("From") String fromNumber,
            @RequestParam("Body") String messageBody,
            @RequestParam("NumMedia") Integer numMedia,
            @RequestParam(name = "MediaContentType0", required = false) String contentTypeAttachment,
            @RequestParam(name = "MediaUrl0", required = false) String mediaUrlAttachment) {
        // Retrieve the ongoing task associated with this phone number (customize the retrieval as needed)
        Optional<Task> optionalTask = taskService.findOngoingTaskByPhoneNumber(fromNumber);
        BotState botState;
        BotContext botContext;
        Task task;
        if (optionalTask.isPresent()) {
            task = optionalTask.get();
            // Map the task's status to a corresponding FSM state.
            botState = botStatesFactory.getStateFromTask(task);
        } else {
            task = Task.builder()
                    .id(new TaskId(fromNumber, LocalDateTime.now()))
                    .user(new User(fromNumber))
                    .status(TaskStatus.INITIAL_STATE)
                    .isWeb(false)
                    .accepted(false)
                    .build();
            // Fallback initial state if no ongoing task exists.
            botState = botStatesFactory.getInitialState();
        }
        // Create the FSM context with the initial state and state factory.
        botContext = new BotContext(botState, task);

        // Wrap the request data into a MessageData object.
        MessageData messageData = new MessageData(messageBody, numMedia, contentTypeAttachment, mediaUrlAttachment);
        // Delegate the processing to the FSM.
        botContext.handle(fromNumber, messageData);

        return ResponseEntity.ok("Message processed successfully.");
    }
    
}
