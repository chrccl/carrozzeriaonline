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
import it.chrccl.carrozzeriaonline.model.dao.TaskStatus;
import it.chrccl.carrozzeriaonline.services.OtpCheckService;
import it.chrccl.carrozzeriaonline.services.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class OTPState implements BotState {

    private final TwilioComponent twilio;

    private final TaskService taskService;

    private final IOComponent ioComponent;

    private final BulkGateComponent bulkGateComponent;

    private final OtpCheckService otpCheckService;

    @Autowired
    public OTPState(TwilioComponent twilio, TaskService taskService,
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
        context.getTask().setStatus(TaskStatus.CAP);
        taskService.save(context.getTask());

        ioComponent.writeOnWarrantFile(context, Partner.CARLINK, Constants.SIGNED_DIGITALLY_STATUS, 380, 105);

        ioComponent.writeOnWarrantFile(context, Partner.CARLINK,
                LocalDate.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)), 110, 105);

        ioComponent.writeOnWarrantFile(context, Partner.SAVOIA, Constants.SIGNED_DIGITALLY_STATUS, 400, 210);

        ioComponent.writeOnWarrantFile(context, Partner.SAVOIA,
                LocalDate.now().format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT)), 108, 205);


        PhoneNumber to = new PhoneNumber(fromNumber);
        twilio.sendMessage(to, Constants.BOT_CAP_MESSAGE);
    }

    @Override
    public Boolean verifyMessage(Task task, MessageData data) {
        OtpCheck otp = otpCheckService.findMostRecentOtpCheckByTask(task);
        if(otp != null) {
            return !bulkGateComponent.verifyOtp(otp.getOtpId(), data.getMessageBody());
        }else{
            return true;
        }
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {
        OtpCheck otp = otpCheckService.findMostRecentOtpCheckByTask(context.getTask());
        PhoneNumber to = new PhoneNumber(fromNumber);
        if(otp != null) {
            bulkGateComponent.resendOtp(otp.getOtpId());
            twilio.sendMessage(to, Constants.BOT_FALLBACK_SENDING_OTP_MESSAGE);
        }else{
            String otpId = bulkGateComponent.sendOtp(extractPhoneNumber(fromNumber));
            otpCheckService.saveOtpCheck(new OtpCheck(LocalDateTime.now(), otpId, context.getTask(), false));
            twilio.sendMessage(to, Constants.BOT_FALLBACK_SENDING_OTP_MESSAGE);
        }
    }

    private String extractPhoneNumber(String fromNumber) {
        return fromNumber.substring(fromNumber.indexOf(':') + 1);
    }

}
