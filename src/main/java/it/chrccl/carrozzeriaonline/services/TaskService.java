package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.dao.Task;
import it.chrccl.carrozzeriaonline.model.dao.TaskStatus;
import it.chrccl.carrozzeriaonline.model.dao.User;
import it.chrccl.carrozzeriaonline.repos.TaskRepo;
import it.chrccl.carrozzeriaonline.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public Task findTaskById(LocalDateTime createdAt) {
        return repo.findById(createdAt).orElse(null);
    }

    public Optional<Task> findOngoingTaskByPhoneNumber(String fromNumber) {
        return repo.findOngoingTaskByPhoneNumber(fromNumber, DEFAULT_EXCLUDED_STATUSES);
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

}
