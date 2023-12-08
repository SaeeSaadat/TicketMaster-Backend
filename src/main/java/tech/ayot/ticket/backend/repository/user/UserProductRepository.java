package tech.ayot.ticket.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.ayot.ticket.backend.model.user.UserProduct;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, Integer> {

    UserProduct findUserProductByUserUsername(String username);
}
