package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.dao.*;
import it.chrccl.carrozzeriaonline.repos.TaskRepo;
import it.chrccl.carrozzeriaonline.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepo repo;

    private final UserRepo userRepo;

    public static final List<TaskStatus> DEFAULT_EXCLUDED_STATUSES = List.of(TaskStatus.INITIAL_STATE);

    @Autowired
    public TaskService(TaskRepo repo, UserRepo userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
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

    public Optional<Task> findOngoingTaskByPhoneNumber(String fromNumber) {
        return repo.findOngoingTaskByPhoneNumber(fromNumber, DEFAULT_EXCLUDED_STATUSES);
    }

    public List<Task> findTasksByRepairCenter(RepairCenter rc) {
        return repo.findTasksByRepairCenter(rc, Sort.by(Sort.Direction.ASC, "dateTime"));
    }

    public List<Task> findTasksByPartner(Partner partner, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dateTime"));
        return repo.findTasksByPartner(partner, pageable);
    }

    public List<Task> findTasksByStatus(TaskStatus status) {
        return repo.findTasksByStatus(status);
    }

    public Task save(Task task) {
        User taskUser = task.getUser();
        Optional<User> existingUser = userRepo.findById(taskUser.getMobilePhone());

        if (existingUser.isPresent() && existingUser.get().equals(taskUser)) {
            task.setUser(existingUser.get());
        } else {
            userRepo.save(taskUser);
        }

        return repo.save(task);
    }

    public void delete(TaskId id) {
        repo.deleteById(id);
    }

}
