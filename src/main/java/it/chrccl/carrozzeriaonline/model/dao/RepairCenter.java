package it.chrccl.carrozzeriaonline.model.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class RepairCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    private String address;

    private String cap;

    private String city;

    private String phoneNumber;

    private String email;

    private Partner partner;

}
