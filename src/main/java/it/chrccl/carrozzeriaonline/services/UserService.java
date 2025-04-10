package it.chrccl.carrozzeriaonline.services;

import it.chrccl.carrozzeriaonline.model.dao.User;
import it.chrccl.carrozzeriaonline.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepo repo;

    @Autowired
    public UserService(UserRepo repo) {
        this.repo = repo;
    }

    public List<User> findAll() {
        return repo.findAll();
    }

    public User findById(String mobilePhone) {
        return repo.findById(mobilePhone).orElse(null);
    }

    public User findUserByCf(String cf) {
        return repo.findUserByCf(cf);
    }

    public User save(User user) {
        return repo.save(user);
    }

    public void delete(User user) {
        repo.delete(user);
    }

}
