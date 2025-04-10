package it.chrccl.carrozzeriaonline.repository;

import it.chrccl.carrozzeriaonline.model.dao.RepairCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReparCenterRepo extends JpaRepository<RepairCenter, Long> {

    List<RepairCenter> findRepairCentersByCompanyNameIsLikeIgnoreCase(String companyName);

}
