package it.chrccl.carrozzeriaonline.model;

import it.chrccl.carrozzeriaonline.model.dao.Attachment;
import it.chrccl.carrozzeriaonline.model.dao.Task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebTask {

    private Task task;

    private List<Attachment> attachments;

    private String companyName;

}
