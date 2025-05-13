package it.chrccl.carrozzeriaonline.model.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BRCPerTaskId {

    @ManyToOne
    @JoinColumn(name = "task_created_at", referencedColumnName = "created_at")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "repair_center_id")
    private RepairCenter repairCenter;

}
