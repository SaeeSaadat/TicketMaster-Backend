package tech.ayot.ticket.backend.model.ticket;

import jakarta.persistence.*;
import tech.ayot.ticket.backend.model.BaseModel;
import tech.ayot.ticket.backend.model.product.Product;
import tech.ayot.ticket.backend.model.user.User;

@Entity
@Table(name = "messages")
public class Message extends BaseModel {

    @Column(length = 4096)
    private String content;

    @ManyToOne
    private Ticket ticket;

    @ManyToOne
    private User user;

}
