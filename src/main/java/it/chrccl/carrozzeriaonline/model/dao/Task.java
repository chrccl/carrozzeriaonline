package it.chrccl.carrozzeriaonline.model.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @EmbeddedId
    private TaskId id;

    private String licensePlate;

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
