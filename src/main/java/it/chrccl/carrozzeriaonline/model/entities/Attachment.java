package it.chrccl.carrozzeriaonline.model.entities;

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

    private String filePath;

    private String url;

    @ManyToOne
    @JoinColumn(name = "task_created_at", referencedColumnName = "created_at")
    private Task task;

}
