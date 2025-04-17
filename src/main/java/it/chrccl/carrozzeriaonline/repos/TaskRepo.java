package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.dao.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepo extends JpaRepository<Task, TaskId> {

    List<Task> findTasksByUser(User user, Sort sort);

    List<Task> findTasksByRepairCenter(RepairCenter repairCenter, Sort sort);

    List<Task> findTasksByPartner(Partner partner, Pageable pageable);

    List<Task> findTasksByStatus(TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.user.mobilePhone = :fromNumber AND t.status NOT IN :excludedStatuses")
    Optional<Task> findOngoingTaskByPhoneNumber(
            @Param("mobilePhone") String mobilePhone,
            @Param("excludedStatuses") List<TaskStatus> excludedStatuses
    );


}
