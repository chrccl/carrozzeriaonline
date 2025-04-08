package it.chrccl.carrozzeriaonline.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    private String mobilePhone;

    private String fullName;

    private String cf;

}
