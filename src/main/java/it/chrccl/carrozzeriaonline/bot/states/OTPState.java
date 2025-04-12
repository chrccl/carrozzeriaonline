package it.chrccl.carrozzeriaonline.bot.states;

import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.MessageData;
import org.springframework.stereotype.Component;

@Component
public class OTPState implements BotState {

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        System.out.println("OTPState.handleMessage");
    }

    @Override
    public Boolean verifyMessage(MessageData data) {
        return null;
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {

    }

}
