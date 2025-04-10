package it.chrccl.carrozzeriaonline.repository;

import it.chrccl.carrozzeriaonline.model.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

    User findUserByCf(String cf);

}
