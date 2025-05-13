package it.chrccl.carrozzeriaonline.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CAP {

    @Id
    private String cap;

    private String city;

    private String province;

    private String provinceCode;

    private String region;

    private Double lat;

    private Double lon;

}
