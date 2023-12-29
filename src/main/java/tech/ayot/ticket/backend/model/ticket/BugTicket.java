package tech.ayot.ticket.backend.model.ticket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "bug_tickets")
public class BugTicket extends Ticket{

    @Column
    private String report;
}
