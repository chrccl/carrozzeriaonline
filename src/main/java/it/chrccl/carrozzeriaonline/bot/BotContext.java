package it.chrccl.carrozzeriaonline.bot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotContext {

    private BotState currentState;

    public BotContext(BotState initState) {
        currentState = initState;
    }

    public void handle(String fromNumber, MessageData message) {
        currentState.handleMessage(this, fromNumber, message);
    }

    /**
     * TODO: helpers methods common to the concrete states
     */

}
