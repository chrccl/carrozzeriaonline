package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.dao.OtpCheck;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpCheckRepo extends JpaRepository<OtpCheck, LocalDateTime> {

    Optional<List<OtpCheck>> findByTask(Task task);

}
