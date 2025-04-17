package it.chrccl.carrozzeriaonline.model.dao;

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

    @EmbeddedId
    private TaskId id;

    private String licensePlate;

    @MapsId("userMobilePhone")
    @ManyToOne
    @JoinColumn(name = "user_mobile_phone")
    private User user;

    private LocalDate accidentDate;

    private String warrantUrl;

    private TaskStatus status;

    private Boolean isWeb;

    private Boolean accepted;

    private LocalDateTime createdAt;

}
