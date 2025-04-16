package it.chrccl.carrozzeriaonline.model.dao;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
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
    @JoinColumns({
            @JoinColumn(name = "task_userMobilePhone", referencedColumnName = "userMobilePhone"),
            @JoinColumn(name = "task_createdAt", referencedColumnName = "createdAt")
    })
    private Task task;

    @ManyToOne
    @JoinColumn(name = "repair_center_id")
    private RepairCenter repairCenter;

}
