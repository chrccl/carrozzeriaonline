package it.chrccl.carrozzeriaonline.bot.states;

import com.twilio.type.PhoneNumber;
import it.chrccl.carrozzeriaonline.bot.BotContext;
import it.chrccl.carrozzeriaonline.bot.BotState;
import it.chrccl.carrozzeriaonline.bot.MessageData;
import it.chrccl.carrozzeriaonline.components.BulkGateComponent;
import it.chrccl.carrozzeriaonline.components.IOComponent;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.dao.OtpCheck;
import it.chrccl.carrozzeriaonline.model.dao.Partner;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.services.OtpCheckService;
import it.chrccl.carrozzeriaonline.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CarLicenseAndPhoneConfState implements BotState {

    private final TwilioComponent twilio;

    private final TaskService taskService;

    private final IOComponent ioComponent;

    private final BulkGateComponent bulkGateComponent;

    private final OtpCheckService otpCheckService;

    @Autowired
    public CarLicenseAndPhoneConfState(TwilioComponent twilio, TaskService taskService,
                                       IOComponent ioComponent, BulkGateComponent bulkGateComponent,
                                       OtpCheckService otpCheckService) {
        this.twilio = twilio;
        this.taskService = taskService;
        this.ioComponent = ioComponent;
        this.bulkGateComponent = bulkGateComponent;
        this.otpCheckService = otpCheckService;
    }

    @Override
    public void handleMessage(BotContext context, String fromNumber, MessageData data) {
        String extractedPhoneNumber = extractPhoneNumber(fromNumber);

        context.getTask().setLicensePlate(data.getMessageBody());
        taskService.save(context.getTask());

        ioComponent.writeOnWarrantFile(context, Partner.CARLINK, extractedPhoneNumber, 190, 592);
        ioComponent.writeOnWarrantFile(context, Partner.CARLINK, data.getMessageBody(), 135, 475);
        ioComponent.writeOnWarrantFile(context, Partner.SAVOIA, data.getMessageBody(), 300, 628);

        String otpId = bulkGateComponent.sendOtp(extractedPhoneNumber);
        otpCheckService.saveOtpCheck(new OtpCheck(LocalDateTime.now(), otpId, context.getTask(), false));

        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(to, Constants.BOT_SENDING_OTP_MESSAGE);
    }

    @Override
    public Boolean verifyMessage(Task task, MessageData data) {
        return false;
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {
        handleMessage(context, fromNumber, data);
    }

    private String extractPhoneNumber(String fromNumber) {
        return fromNumber.substring(fromNumber.indexOf(':') + 1);
    }

}
