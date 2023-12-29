package tech.ayot.ticket.backend.model.ticket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_tickets")
public class QuestionTicket extends Ticket{

}
