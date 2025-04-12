package it.chrccl.carrozzeriaonline.bot;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageData {

    private String messageBody;

    private int numMedia;

    private String contentTypeAttachment;

    private String mediaUrlAttachment;

}
