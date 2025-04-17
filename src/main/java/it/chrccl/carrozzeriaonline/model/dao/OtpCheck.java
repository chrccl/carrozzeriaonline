package it.chrccl.carrozzeriaonline.model.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpCheck {

    @Id
    private LocalDateTime timestamp;

    private String otpId;

    @ManyToOne
    @JoinColumn(name = "task_created_at", referencedColumnName = "created_at")
    private Task task;

    private Boolean confirmed;

}
