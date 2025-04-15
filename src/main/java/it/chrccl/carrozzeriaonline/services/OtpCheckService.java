package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.dao.OtpCheck;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.repos.OtpCheckRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OtpCheckService {

    private final OtpCheckRepo repo;

    @Autowired
    public OtpCheckService(OtpCheckRepo repo) {
        this.repo = repo;
    }

    public List<OtpCheck> findAllOtpChecks() {
        return repo.findAll();
    }

    public List<OtpCheck> findOtpChecksByTask(Task task) {
        Optional<List<OtpCheck>> otpChecks = repo.findByTask(task);
        return otpChecks.orElseGet(ArrayList::new);
    }

    public OtpCheck saveOtpCheck(OtpCheck otpCheck) {
        return repo.save(otpCheck);
    }

    public void deleteOtpCheck(OtpCheck otpCheck) {
        repo.delete(otpCheck);
    }

}
