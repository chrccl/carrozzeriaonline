package it.chrccl.carrozzeriaonline.model.bot;

import it.chrccl.carrozzeriaonline.model.entities.Task;
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

    public void handleError(String fromNumber, MessageData message) {
        currentState.handleError(this, fromNumber, message);
    }

}
