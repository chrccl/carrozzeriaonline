package it.chrccl.carrozzeriaonline.bot;

import it.chrccl.carrozzeriaonline.model.dao.Task;

public interface BotState {

    void handleMessage(BotContext context, String fromNumber, MessageData data);

    Boolean verifyMessage(Task task, MessageData data);

    void handleError(BotContext context, String fromNumber, MessageData data);

}
