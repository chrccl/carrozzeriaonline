package it.chrccl.carrozzeriaonline.bot.states;

import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.MessageData;

public class MultimediaState implements BotState {

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        System.out.println("MultimediaState.handleMessage");
    }

}
