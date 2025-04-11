package it.chrccl.carrozzeriaonline.model.dao;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
public class TaskId implements Serializable {

    private String userMobilePhone;

    private String licensePlate;

    private LocalDateTime createdAt;

}
