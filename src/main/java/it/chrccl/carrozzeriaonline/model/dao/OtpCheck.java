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
    @JoinColumns({
            @JoinColumn(name = "task_userMobilePhone", referencedColumnName = "userMobilePhone"),
            @JoinColumn(name = "task_createdAt", referencedColumnName = "createdAt")
    })
    private Task task;

    private Boolean confirmed;

}
