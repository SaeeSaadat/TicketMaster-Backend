package tech.ayot.ticket.backend.model.ticket;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "suggestion_ticket")
public class SuggestionTicket extends Ticket{

}
