package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.dao.*;
import it.chrccl.carrozzeriaonline.repos.TaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepo repo;

    @Autowired
    public TaskService(TaskRepo repo) {
        this.repo = repo;
    }

    public List<Task> findAllTasks() {
        return repo.findAll();
    }

    public Task findTaskById(TaskId id) {
        return repo.findById(id).orElse(null);
    }

    public List<Task> findTasksByUser(User user){
        return repo.findTasksByUser(user, Sort.by(Sort.Direction.ASC, "dateTime"));
    }

    public List<Task> findTasksByRepairCenter(RepairCenter rc) {
        return repo.findTasksByRepairCenter(rc, Sort.by(Sort.Direction.ASC, "dateTime"));
    }

    public List<Task> findTasksByPartner(Partner partner, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dateTime"));
        return repo.findTasksByPartner(partner, pageable);
    }

    public Task save(Task task) {
        return repo.save(task);
    }

    public void delete(TaskId id) {
        repo.deleteById(id);
    }

}
