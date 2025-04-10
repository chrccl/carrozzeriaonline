package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.dao.*;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, TaskId> {

    List<Task> findTasksByUser(User user, Sort sort);

    List<Task> findTasksByRepairCenter(RepairCenter repairCenter, Sort sort);

    List<Task> findTasksByPartner(Partner partner, Pageable pageable);

}
