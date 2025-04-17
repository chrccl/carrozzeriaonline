package it.chrccl.carrozzeriaonline.model.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class User {

    @Id
    private String mobilePhone;

    private String fullName;

    private String cf;

    private String preferredCap;

    public User(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

}
