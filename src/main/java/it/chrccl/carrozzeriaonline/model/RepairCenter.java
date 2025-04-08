package it.chrccl.carrozzeriaonline.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class RepairCenter {

    @Id
    private Integer id;

    private String companyName;

    private String address;

    private String cap;

    private String city;

    private String phoneNumber;

    private String email;

}
