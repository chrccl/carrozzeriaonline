package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.dao.RepairCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairCenterRepo extends JpaRepository<RepairCenter, Long> {

    List<RepairCenter> findRepairCentersByCompanyNameIsLikeIgnoreCase(String companyName);

}
