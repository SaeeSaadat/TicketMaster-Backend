package tech.ayot.ticket.backend.repository.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.ayot.ticket.backend.model.enumuration.TicketStatus;
import tech.ayot.ticket.backend.model.enumuration.TicketType;
import tech.ayot.ticket.backend.model.ticket.Ticket;
import tech.ayot.ticket.backend.model.user.User;

import java.util.Date;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Ticket findTicketById(Long id);

    List<Ticket> findTicketsByCreatedBy(User user);

    @Query(
        "select t from Ticket t " +
            "where (:userId is null or t.createdBy.id = :userId) " +
            "and (:type is null or t.type = :type) " +
            "and (:productName is null or t.product.name = :productName) " +
            "and (t.creationDate >= cast(:createdAfter as date)) " +
            "and (t.creationDate <= cast(:createdBefore as date)) " +
            "and (:status is null or t.status = :status)"
    )
    Page<Ticket> listAllByUser(
        Long userId,
        TicketType type,
        String productName,
        Date createdAfter,
        Date createdBefore,
        TicketStatus status,
        Pageable pageable
    );
}
