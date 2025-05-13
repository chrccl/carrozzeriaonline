package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.entities.CAP;
import it.chrccl.carrozzeriaonline.repos.CapRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CapService {

    private final CapRepo repo;

    @Autowired
    public CapService(CapRepo repo) {
        this.repo = repo;
    }

    
    public CAP findCAPByCodeOrClosest(String capCode) {
        return repo.findById(capCode).orElseGet(() -> {
            try {
                int targetVal = Integer.parseInt(capCode);
                return repo.findClosestCAPByNumericalOrder(targetVal);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Il CAP fornito non è numerico: " + capCode, e);
            }
        });
    }

}
