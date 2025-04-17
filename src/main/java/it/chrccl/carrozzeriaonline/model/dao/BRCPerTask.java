package it.chrccl.carrozzeriaonline.model.dao;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BRCPerTask {

    @EmbeddedId
    private BRCPerTaskId BRCPerTaskId;

    private LocalDateTime assignedAt;

    private Boolean accepted;

}
