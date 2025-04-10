package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.dao.RepairCenter;
import it.chrccl.carrozzeriaonline.repos.RepairCenterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairCenterService {

    private final RepairCenterRepo repo;

    @Autowired
    public RepairCenterService(RepairCenterRepo repo) {
        this.repo = repo;
    }

    public List<RepairCenter> findAllRepairCenters() {
        return repo.findAll();
    }

    public RepairCenter findRepairCenterById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<RepairCenter> findRepairCentersByCompanyNameIsLikeIgnoreCase(String companyName) {
        return repo.findRepairCentersByCompanyNameIsLikeIgnoreCase(companyName);
    }

    public RepairCenter save(RepairCenter repairCenter) {
        return repo.save(repairCenter);
    }

    public void delete(RepairCenter repairCenter) {
        repo.delete(repairCenter);
    }

}
