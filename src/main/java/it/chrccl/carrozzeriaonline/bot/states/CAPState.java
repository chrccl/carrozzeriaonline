package it.chrccl.carrozzeriaonline.bot.states;

import com.twilio.type.PhoneNumber;
import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.MessageData;

import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.dao.RepairCenter;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.model.dao.TaskStatus;
import it.chrccl.carrozzeriaonline.services.RepairCenterService;
import it.chrccl.carrozzeriaonline.services.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CAPState implements BotState {

    private final TwilioComponent twilio;

    private final TaskService taskService;

    private final RepairCenterService repairCenterService;

    @Autowired
    public CAPState(TwilioComponent twilio, TaskService taskService, RepairCenterService repairCenterService) {
        this.twilio = twilio;
        this.taskService = taskService;
        this.repairCenterService = repairCenterService;
    }


    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        context.getTask().setStatus(TaskStatus.CAR_REPAIR_CENTER);
        context.getTask().getUser().setPreferredCap(data.getMessageBody());
        taskService.save(context.getTask());

        List<RepairCenter> closestRepairCentersByCap = repairCenterService.findClosestRepairCentersByCap(
                data.getMessageBody(), null
        );

        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendRepairCentersProposalMessage(to, closestRepairCentersByCap);
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
