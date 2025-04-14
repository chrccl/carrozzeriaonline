package it.chrccl.carrozzeriaonline.bot.states;

import com.twilio.type.PhoneNumber;

import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.MessageData;
import it.chrccl.carrozzeriaonline.components.IOComponent;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.dao.Partner;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.model.dao.TaskStatus;
import it.chrccl.carrozzeriaonline.services.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class FullNameState implements BotState {

    private final TwilioComponent twilio;

    private final TaskService taskService;

    private final IOComponent ioComponent;

    @Autowired
    public FullNameState(TwilioComponent twilio, TaskService taskService, IOComponent ioComponent) {
        this.twilio = twilio;
        this.taskService = taskService;
        this.ioComponent = ioComponent;
    }

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        String fullName = capitalizeWords(data.getMessageBody());
        context.getTask().getUser().setFullName(fullName);
        context.getTask().setStatus(TaskStatus.CF_OR_PIVA);
        taskService.save(context.getTask());

        ioComponent.writeOnWarrantFile(context, Partner.CARLINK, fullName, 190, 652);
        ioComponent.writeOnWarrantFile(context, Partner.SAVOIA, fullName, 185, 643);
        ioComponent.signWarrantFile(Partner.CARLINK, fullName, 370, 75);
        ioComponent.signWarrantFile(Partner.SAVOIA, fullName, 390, 185);

        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(to, Constants.BOT_CF_OR_PIVA_MESSAGE);
    }

    @Override
    public Boolean verifyMessage(Task task,  MessageData data) {
        return !isFullName(data.getMessageBody());
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {
        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(to, Constants.BOT_CF_OR_PIVA_MESSAGE);
    }

    private String capitalizeWords(String input) {
        return Arrays.stream(input.trim().split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    private Boolean isFullName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        name = name.trim();

        return name.matches("^\\s*\\S+(\\s+\\S+)+\\s*$");
    }

}
