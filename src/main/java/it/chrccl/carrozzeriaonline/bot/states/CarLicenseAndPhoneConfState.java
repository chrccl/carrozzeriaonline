package it.chrccl.carrozzeriaonline.bot.states;

import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.MessageData;
import it.chrccl.carrozzeriaonline.components.IOComponent;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.dao.Partner;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarLicenseState implements BotState {

    private final TwilioComponent twilio;

    private final TaskService taskService;

    private final IOComponent ioComponent;

    @Autowired
    public CarLicenseState(TwilioComponent twilio, TaskService taskService, IOComponent ioComponent) {
        this.twilio = twilio;
        this.taskService = taskService;
        this.ioComponent = ioComponent;
    }

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        context.getTask().setLicensePlate(data.getMessageBody());
        taskService.save(context.getTask());

        ioComponent.writeOnWarrantFile(context, Partner.CARLINK, data.getMessageBody(), 135, 475);
        ioComponent.writeOnWarrantFile(context, Partner.SAVOIA, data.getMessageBody(), 135, 475);
    }

    @Override
    public Boolean verifyMessage(Task task, MessageData data) {
        return false;
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {
        handleMessage(context, fromNumber, data);
    }

}
