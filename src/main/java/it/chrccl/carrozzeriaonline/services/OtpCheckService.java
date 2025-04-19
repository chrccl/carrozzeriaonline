package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.dao.OtpCheck;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.repos.OtpCheckRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OtpCheckService {

    private final OtpCheckRepo repo;

    @Autowired
    public OtpCheckService(OtpCheckRepo repo) {
        this.repo = repo;
    }

    public OtpCheck findMostRecentOtpCheckByTask(Task task) {
        Optional<OtpCheck> otpChecks = repo.findTopByTaskOrderByTimestampDesc(task);
        return otpChecks.orElse(null);
    }

    public OtpCheck findByOtpId(String otpId) {
        return repo.findByOtpId(otpId).orElse(null);
    }

    public void saveOtpCheck(OtpCheck otpCheck) {
        repo.save(otpCheck);
    }

}
