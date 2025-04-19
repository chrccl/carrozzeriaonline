package it.chrccl.carrozzeriaonline.model.bot;

import it.chrccl.carrozzeriaonline.model.dao.Task;

public interface BotState {

    void handleMessage(BotContext context, String fromNumber, MessageData data);

    Boolean verifyMessage(Task task, MessageData data);

    void handleError(BotContext context, String fromNumber, MessageData data);

    default String extractPhoneNumber(String fromNumber) {
        return fromNumber.substring(fromNumber.indexOf(':') + 1);
    }

}
