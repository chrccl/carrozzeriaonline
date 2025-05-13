package it.chrccl.carrozzeriaonline.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "user_mobile_phone", referencedColumnName = "user_mobile_phone")
    private User user;

    private LocalDate accidentDate;

    private String warrantUrl;

    private TaskStatus status;

    private Boolean isWeb;

    private Boolean accepted;

}
