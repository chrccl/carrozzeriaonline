package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.dao.BRCPerTask;
import it.chrccl.carrozzeriaonline.model.dao.BRCPerTaskId;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BRCPerTaskRepo extends JpaRepository<BRCPerTask, BRCPerTaskId> {

    Optional<List<BRCPerTask>> findBRCPerTasksByBCRPerTaskID_Task(Task task);

}
