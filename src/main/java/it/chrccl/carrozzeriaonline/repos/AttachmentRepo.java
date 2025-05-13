package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.entities.Attachment;
import it.chrccl.carrozzeriaonline.model.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepo extends JpaRepository<Attachment, Long> {

    List<Attachment> findAttachmentsByTask(Task task);

}
