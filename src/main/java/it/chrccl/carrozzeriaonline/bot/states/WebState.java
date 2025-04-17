package it.chrccl.carrozzeriaonline.bot.states;

import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.MessageData;
import it.chrccl.carrozzeriaonline.model.dao.Task;

import org.springframework.stereotype.Component;

@Component
public class WebState implements BotState {

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {

    }

    @Override
    public Boolean verifyMessage(Task task, MessageData data) {
        return null;
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {

    }
}
