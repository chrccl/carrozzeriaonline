package it.chrccl.carrozzeriaonline.bot;

import it.chrccl.carrozzeriaonline.model.dao.Task;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotContext {

    private BotState currentState;

    private Task task;

    public BotContext(BotState initState, Task task) {
        currentState = initState;
        this.task = task;
    }

    public void handle(String fromNumber, MessageData message) {
        currentState.handleMessage(this, fromNumber, message);
    }

    /**
     * TODO: helpers methods common to the concrete states
     */

}
