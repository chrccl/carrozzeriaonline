package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.dao.CAP;
import it.chrccl.carrozzeriaonline.model.dao.RepairCenter;
import it.chrccl.carrozzeriaonline.repos.RepairCenterRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RepairCenterService {

    private final RepairCenterRepo repo;

    private final CapService capService;

    @Autowired
    public RepairCenterService(RepairCenterRepo repo, CapService capService) {
        this.repo = repo;
        this.capService = capService;
    }

    public List<RepairCenter> findClosestRepairCentersByCap(String targetCap, List<RepairCenter> centersToAvoid) {
        if (targetCap == null) return Collections.emptyList();

        // Retrieve target CAP or its closest numerical alternative.
        CAP target = capService.findCAPByCodeOrClosest(targetCap);

        // Get ordered centers from the DB, then filter out those to avoid.
        List<RepairCenter> filtered = repo.findRepairCentersOrderedByDistance(target.getLat(), target.getLon())
                .stream()
                .filter(rc -> centersToAvoid == null || !centersToAvoid.contains(rc))
                .collect(Collectors.toList());

        // First pass: add up to 3 centers with unique company names.
        List<RepairCenter> result = new ArrayList<>();
        Set<String> seenNames = new HashSet<>();
        for (RepairCenter rc : filtered) {
            if (seenNames.add(rc.getCompanyName()) && result.size() < 3) {
                result.add(rc);
            }
        }
        // Second pass: complete result with remaining centers preserving order.
        filtered.stream()
                .filter(rc -> !result.contains(rc))
                .forEach(result::add);

        return result;
    }

    public List<RepairCenter> findAllRepairCenters() {
        return repo.findAll();
    }

    public RepairCenter findRepairCenterById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public RepairCenter findRepairCentersByCompanyNameIsLikeIgnoreCase(String companyName) {
        return repo.findRepairCentersByCompanyNameIsLikeIgnoreCase(companyName).orElse(null);
    }

    public List<RepairCenter> findRepairCentersByCAP(String cap) {
        return repo.findRepairCentersByCap(cap);
    }

    public RepairCenter save(RepairCenter repairCenter) {
        return repo.save(repairCenter);
    }

    public void delete(RepairCenter repairCenter) {
        repo.delete(repairCenter);
    }

}
