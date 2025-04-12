package it.chrccl.carrozzeriaonline.bot.states;

import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.MessageData;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InitialState implements BotState {

    private final TwilioComponent twilio;

    @Autowired
    public InitialState(TwilioComponent twilio) {
        this.twilio = twilio;
    }

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        System.out.println("InitialState.handleMessage");
    }

    @Override
    public Boolean verifyMessage(MessageData data) {
        return null;
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {

    }

}
