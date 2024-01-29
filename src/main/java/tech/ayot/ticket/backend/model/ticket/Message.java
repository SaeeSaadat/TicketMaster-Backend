package tech.ayot.ticket.backend.model.ticket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tech.ayot.ticket.backend.model.BaseModel;

@Entity
@Table(name = "messages")
public class Message extends BaseModel {

    @ManyToOne
    private Ticket ticket;

    @Column(length = 4096)
    private String content;


    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
