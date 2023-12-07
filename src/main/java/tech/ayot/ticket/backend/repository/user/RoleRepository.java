package tech.ayot.ticket.backend.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.ayot.ticket.backend.model.user.Role;

/**
 * Repository for role entity
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
