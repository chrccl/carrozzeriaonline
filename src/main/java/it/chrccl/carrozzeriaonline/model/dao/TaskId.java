package it.chrccl.carrozzeriaonline.model.dao;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskId implements Serializable {

    private String userMobilePhone;

    private LocalDateTime createdAt;

}
