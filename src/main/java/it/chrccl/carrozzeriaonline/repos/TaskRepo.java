package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.entities.Task;
import it.chrccl.carrozzeriaonline.model.entities.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepo extends JpaRepository<Task, LocalDateTime> {

    List<Task> findTasksByStatus(TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.user.mobilePhone = :fromNumber AND t.status NOT IN :excludedStatuses")
    Optional<Task> findOngoingTaskByPhoneNumber(
            @Param("fromNumber") String fromNumber,
            @Param("excludedStatuses") List<TaskStatus> excludedStatuses
    );

    void deleteByCreatedAt(LocalDateTime createdAt);

}
