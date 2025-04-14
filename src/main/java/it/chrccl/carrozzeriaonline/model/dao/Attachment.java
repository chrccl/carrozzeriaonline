package it.chrccl.carrozzeriaonline.model.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String contentType;

    private String base64Data;

    private String url;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "user_mobile_phone", referencedColumnName = "user_mobile_phone"),
            @JoinColumn(name = "license_plate", referencedColumnName = "licensePlate"),
            @JoinColumn(name = "date_time", referencedColumnName = "dateTime")
    })
    private Task task;

}
