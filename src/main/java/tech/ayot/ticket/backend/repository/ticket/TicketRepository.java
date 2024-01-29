package tech.ayot.ticket.backend.repository.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.ayot.ticket.backend.model.ticket.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
