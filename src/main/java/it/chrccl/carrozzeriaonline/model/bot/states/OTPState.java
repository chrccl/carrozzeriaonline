package it.chrccl.carrozzeriaonline.model.bot.states;

import com.twilio.type.PhoneNumber;
import it.chrccl.carrozzeriaonline.components.BulkGateComponent;
import it.chrccl.carrozzeriaonline.components.IOComponent;
import it.chrccl.carrozzeriaonline.components.TwilioComponent;
import it.chrccl.carrozzeriaonline.model.Constants;
import it.chrccl.carrozzeriaonline.model.bot.BotContext;
import it.chrccl.carrozzeriaonline.model.bot.BotState;
import it.chrccl.carrozzeriaonline.model.bot.MessageData;
import it.chrccl.carrozzeriaonline.model.entities.OtpCheck;
import it.chrccl.carrozzeriaonline.model.entities.Partner;
import it.chrccl.carrozzeriaonline.model.entities.Task;
import it.chrccl.carrozzeriaonline.model.entities.TaskStatus;
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
            if(LocalDateTime.now().minusMinutes(15).isBefore(otp.getTimestamp()) || task.getIsWeb()) {
                Boolean isWrong = !bulkGateComponent.verifyOtp(otp.getOtpId(), data.getMessageBody());
                if (!isWrong) {
                    otp.setConfirmed(true);
                    otpCheckService.saveOtpCheck(otp);
                }
                return isWrong;
            }else{
                taskService.delete(task);
                data.setExpired(true);
                return true;
            }
        }else{
            return true;
        }
    }

    @Override
    public void handleError(BotContext context, String fromNumber, MessageData data) {
        PhoneNumber to = new PhoneNumber(fromNumber);
        if(data.isExpired()) {
            twilio.sendMessage(to, Constants.BOT_FALLBACK_EXPIRED_OTP_MESSAGE);
        }else{
            OtpCheck otp = otpCheckService.findMostRecentOtpCheckByTask(context.getTask());
            if(otp != null) {
                bulkGateComponent.resendOtp(otp.getOtpId());
                twilio.sendMessage(to, Constants.BOT_FALLBACK_SENDING_OTP_MESSAGE);
            }else{
                String otpId = bulkGateComponent.sendOtp(extractPhoneNumber(fromNumber));
                otpCheckService.saveOtpCheck(new OtpCheck(LocalDateTime.now(), otpId, context.getTask(), false));
                twilio.sendMessage(to, Constants.BOT_FALLBACK_SENDING_OTP_MESSAGE);
            }
        }
    }

}
