package it.chrccl.carrozzeriaonline.bot;

public interface BotState {

    void handleMessage(BotContext context, String fromNumber, MessageData data);

    Boolean verifyMessage(MessageData data);

    void handleError(BotContext context, String fromNumber, MessageData data);

}
