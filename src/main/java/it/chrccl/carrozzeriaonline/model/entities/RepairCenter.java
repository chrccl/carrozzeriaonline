package it.chrccl.carrozzeriaonline.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RepairCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String companyName;

    private String address;

    private String cap;

    private String city;

    private String phoneNumber;

    private String email;

    private Partner partner;

}
