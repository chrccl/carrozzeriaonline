package it.chrccl.carrozzeriaonline.bot;

import it.chrccl.carrozzeriaonline.bot.states.*;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.model.dao.TaskStatus;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BotStatesFactory {

    private final InitialState initialState;

    private final WebState webState;

    private final MultimediaState multimediaState;

    private final DateState dateState;

    private final FullNameState fullNameState;

    private final CFOrPIVAState cfOrPIVAState;

    private final CarLicenseAndPhoneConfState carLicenseAndPhoneConfState;

    private final OTPState otpState;

    private final CAPState capState;

    private final CarRepairCenterState carRepairCenterState;

    private final BouncingState bouncingState;

    @Autowired
    public BotStatesFactory(InitialState initialState, WebState webState, MultimediaState multimediaState,
                            DateState dateState, FullNameState fullNameState, CFOrPIVAState cfOrPIVAState,
                            CarLicenseAndPhoneConfState carLicenseAndPhoneConfState, OTPState otpState, CAPState capState,
                            CarRepairCenterState carRepairCenterState, BouncingState bouncingState) {
        this.initialState = initialState;
        this.webState = webState;
        this.multimediaState = multimediaState;
        this.dateState = dateState;
        this.fullNameState = fullNameState;
        this.cfOrPIVAState = cfOrPIVAState;
        this.carLicenseAndPhoneConfState = carLicenseAndPhoneConfState;
        this.otpState = otpState;
        this.capState = capState;
        this.carRepairCenterState = carRepairCenterState;
        this.bouncingState = bouncingState;
    }

    public BotState getStateFromTask(Task task) {
        TaskStatus status = task.getStatus();
        return switch (status) {
            case INITIAL_STATE -> initialState;
            case MULTIMEDIA    -> multimediaState;
            case DATE          -> dateState;
            case FULL_NAME     -> fullNameState;
            case CF_OR_PIVA    -> cfOrPIVAState;
            case CAR_LICENSE   -> carLicenseAndPhoneConfState;
            case OTP           -> otpState;
            case CAP           -> capState;
            case CAR_REPAIR_CENTER -> carRepairCenterState;
            case BOUNCING -> bouncingState;
            case WEB           -> webState;
            default -> throw new IllegalStateException("Unexpected value: " + status);
        };
    }

}
