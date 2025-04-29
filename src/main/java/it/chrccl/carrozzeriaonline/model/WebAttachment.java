package it.chrccl.carrozzeriaonline.model;

import it.chrccl.carrozzeriaonline.model.dao.Task;
import lombok.Data;

@Data
public class WebAttachment {

    private Long id;

    private String name;

    private String contentType;

    private String base64Data;

    private String url;

    private Task task;

}
