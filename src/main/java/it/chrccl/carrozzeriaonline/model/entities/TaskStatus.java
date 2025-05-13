package it.chrccl.carrozzeriaonline.model.entities;

import lombok.Getter;

@Getter
public enum TaskStatus {

    INITIAL_STATE("INITIAL_STATE"),
    WEB("WEB"),
    MULTIMEDIA("MULTIMEDIA"),
    DATE("DATE"),
    FULL_NAME("FULL_NAME"),
    CF_OR_PIVA("CF_OR_PIVA"),
    CAR_LICENSE("CAR_LICENSE"),
    OTP("OTP"),
    CAP("CAP"),
    CAR_REPAIR_CENTER("CAR_REPAIR_CENTER"),
    BOUNCING("BOUNCING"),
    ACCEPTED("ACCEPTED"),
    DELETED("DELETED");

    private final String name;

    TaskStatus(String name) {
        this.name = name;
    }

}
