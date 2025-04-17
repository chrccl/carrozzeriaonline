package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.dao.BRCPerTask;
import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.repos.BRCPerTaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BRCPerTaskService {

    private final BRCPerTaskRepo repo;

    @Autowired
    public BRCPerTaskService(BRCPerTaskRepo repo) {
        this.repo = repo;
    }

    public List<BRCPerTask> findByTask(Task task) {
        return repo.findBRCPerTasksByBRCPerTaskId_Task(task).orElse(null);
    }

    public BRCPerTask save(BRCPerTask brcPerTask) {
        return repo.save(brcPerTask);
    }

}
