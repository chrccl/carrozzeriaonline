package it.chrccl.carrozzeriaonline.bot;

import lombok.Data;

@Data
public class MessageData {

    private String messageBody;

    private int numMedia;

    private String contentTypeAttachment;

    private String mediaUrlAttachment;

}
