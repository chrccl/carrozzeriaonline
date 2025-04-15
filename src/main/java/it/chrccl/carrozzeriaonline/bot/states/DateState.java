package it.chrccl.carrozzeriaonline.bot.states;

import com.twilio.type.PhoneNumber;
import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.MessageData;
import it.chrccl.carrozzeriaonline.components.IOComponent;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.dao.Partner;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.model.dao.TaskStatus;
import it.chrccl.carrozzeriaonline.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Component
public class DateState implements BotState {

    private final TwilioComponent twilio;

    private final TaskService taskService;

    private final IOComponent ioComponent;

    @Autowired
    public DateState(TwilioComponent twilio, TaskService taskService, IOComponent ioComponent) {
        this.twilio = twilio;
        this.taskService = taskService;
        this.ioComponent = ioComponent;
    }

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        context.getTask().setAccidentDate(LocalDate.parse(data.getMessageBody()));
        context.getTask().setStatus(TaskStatus.FULL_NAME);
        taskService.save(context.getTask());

        ioComponent.writeOnWarrantFile(
                context, Partner.CARLINK, extractPhoneNumber(fromNumber), data.getMessageBody(), 425, 475
        );
        ioComponent.writeOnWarrantFile(
                context, Partner.SAVOIA, extractPhoneNumber(fromNumber), data.getMessageBody(), 115, 585
        );

        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(to, Constants.BOT_FULLNAME_MESSAGE);
    }

    @Override
    public Boolean verifyMessage(Task task, MessageData data) {
        return !(isValidDate(data.getMessageBody(), Constants.DATE_FORMAT)
                || isValidDate(data.getMessageBody(), Constants.REVERSE_DATE_FORMAT));
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {
        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(to, Constants.BOT_FALLBACK_DATE_MESSAGE);
    }

    private static boolean isValidDate(String date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            Date inputDate = sdf.parse(date);
            Date today = new Date();
            // Check if the input date is before or equal to today
            return !inputDate.after(today);
        } catch (ParseException e) {
            return false;
        }
    }

}
