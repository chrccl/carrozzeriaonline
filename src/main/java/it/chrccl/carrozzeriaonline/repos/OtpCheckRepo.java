package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.entities.OtpCheck;
import it.chrccl.carrozzeriaonline.model.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpCheckRepo extends JpaRepository<OtpCheck, LocalDateTime> {

    Optional<OtpCheck> findByOtpId(String otpId);

    Optional<OtpCheck> findTopByTaskOrderByTimestampDesc(Task task);

}
