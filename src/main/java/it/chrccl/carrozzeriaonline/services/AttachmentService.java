package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.dao.Attachment;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.repos.AttachmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttachmentService {

    private final AttachmentRepo repo;

    @Autowired
    public AttachmentService(AttachmentRepo repo) {
        this.repo = repo;
    }

    public List<Attachment> findAttachmentsByTask(Task task) {
        return repo.findAttachmentsByTask(task);
    }

    public Attachment save(Attachment attachment) {
        return repo.save(attachment);
    }

    public List<Attachment> saveAll(List<Attachment> attachments) {
        return repo.saveAll(attachments);
    }

}
