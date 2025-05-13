package it.chrccl.carrozzeriaonline.repos;

import it.chrccl.carrozzeriaonline.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findUserByMobilePhone(String mobilePhone);

    User findUserByCf(String cf);

}
