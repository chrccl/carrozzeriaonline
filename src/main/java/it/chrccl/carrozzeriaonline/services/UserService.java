package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.dao.User;
import it.chrccl.carrozzeriaonline.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo repo;

    @Autowired
    public UserService(UserRepo repo) {
        this.repo = repo;
    }

    public User save(User user) {
        return repo.save(user);
    }

}
