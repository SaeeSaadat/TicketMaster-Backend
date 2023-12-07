package tech.ayot.ticket.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.ayot.ticket.backend.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserByUsername(String username);
}
