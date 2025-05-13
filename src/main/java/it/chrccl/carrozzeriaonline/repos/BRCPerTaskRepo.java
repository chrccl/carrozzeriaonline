package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.entities.BRCPerTask;
import it.chrccl.carrozzeriaonline.model.entities.BRCPerTaskId;
import it.chrccl.carrozzeriaonline.model.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BRCPerTaskRepo extends JpaRepository<BRCPerTask, BRCPerTaskId> {

    Optional<List<BRCPerTask>> findBRCPerTasksByBRCPerTaskId_Task(Task task);

}
