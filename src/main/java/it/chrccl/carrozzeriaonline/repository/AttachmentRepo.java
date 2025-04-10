package it.chrccl.carrozzeriaonline.repository;

import it.chrccl.carrozzeriaonline.model.dao.Attachment;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepo extends JpaRepository<Attachment, Long> {

    List<Attachment> findAttachmentsByTask(Task task);

}
