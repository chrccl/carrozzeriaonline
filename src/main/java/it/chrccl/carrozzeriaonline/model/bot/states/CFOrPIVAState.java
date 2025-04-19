package it.chrccl.carrozzeriaonline.model.bot.states;

import com.twilio.type.PhoneNumber;
import it.chrccl.carrozzeriaonline.components.IOComponent;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.bot.BotContext;
import it.chrccl.carrozzeriaonline.model.bot.BotState;
import it.chrccl.carrozzeriaonline.model.bot.MessageData;
import it.chrccl.carrozzeriaonline.model.dao.Partner;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.model.dao.TaskStatus;
import it.chrccl.carrozzeriaonline.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CFOrPIVAState implements BotState {

    private final TwilioComponent twilio;

    private final TaskService taskService;

    private final IOComponent ioComponent;

    @Autowired
    public CFOrPIVAState(TwilioComponent twilio, TaskService taskService, IOComponent ioComponent) {
        this.twilio = twilio;
        this.taskService = taskService;
        this.ioComponent = ioComponent;
    }

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        String cfOrPiva = data.getMessageBody().toUpperCase();
        context.getTask().getUser().setCf(cfOrPiva);
        context.getTask().setStatus(TaskStatus.CAR_LICENSE);
        taskService.save(context.getTask());

        ioComponent.writeOnWarrantFile(
                context, Partner.CARLINK, extractPhoneNumber(fromNumber), cfOrPiva, 190, 632
        );
        ioComponent.writeOnWarrantFile(
                context, Partner.SAVOIA, extractPhoneNumber(fromNumber), cfOrPiva, 400, 643
        );

        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(to, Constants.BOT_CAR_LICENCE_MESSAGE);
    }

    @Override
    public Boolean verifyMessage(Task task, MessageData data) {
        return !(isValidCF(data.getMessageBody().toUpperCase()) || isValidPIVA(data.getMessageBody().toUpperCase()));
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {
        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(to, Constants.BOT_FALLBACK_CF_OR_PIVA_MESSAGE);
    }

    private boolean isValidCF(String cf) {
        String cfRegex = "^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$";
        return cf != null && cf.matches(cfRegex);
    }

    private boolean isValidPIVA(String pIva) {
        String pIvaRegex = "^[0-9]{11}$";
        if (pIva == null || !pIva.matches(pIvaRegex)) {
            return false;
        }
        return validatePIVA(pIva);
    }

    private boolean validatePIVA(String pIva) {
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            int digit = Character.getNumericValue(pIva.charAt(i));
            if (i % 2 == 0) {
                sum += digit;
            } else {
                int doubled = digit * 2;
                sum += doubled > 9 ? doubled - 9 : doubled;
            }
        }
        int controlDigit = Character.getNumericValue(pIva.charAt(10));
        return (sum + controlDigit) % 10 == 0;
    }

}
