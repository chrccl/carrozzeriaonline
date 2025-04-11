package it.chrccl.carrozzeriaonline.model.dao;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Task {

    @EmbeddedId
    private TaskId id;

    @MapsId("userMobilePhone")
    @ManyToOne
    @JoinColumn(name = "user_mobile_phone")
    private User user;

    @ManyToOne
    @JoinColumn(name = "repair_center_id")
    private RepairCenter repairCenter;

    private LocalDate accidentDate;

    private Partner partner;

    private TaskStatus status;

    private Boolean isWeb;

    private Boolean accepted;

}
