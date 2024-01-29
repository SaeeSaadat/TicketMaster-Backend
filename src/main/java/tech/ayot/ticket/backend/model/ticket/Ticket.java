package tech.ayot.ticket.backend.model.ticket;

import jakarta.persistence.*;
import tech.ayot.ticket.backend.model.BaseModel;
import tech.ayot.ticket.backend.model.enumuration.TicketStatus;
import tech.ayot.ticket.backend.model.enumuration.TicketType;
import tech.ayot.ticket.backend.model.product.Product;

import java.util.Date;

@Entity
@Table(name = "tickets")
public class Ticket extends BaseModel {

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private TicketType type;

    @ManyToOne(optional = false)
    private Product product;

    @Column(length = 64, nullable = false)
    private String title;

    @Column(length = 4096)
    private String description;

    @Column
    private Date deadline;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private TicketStatus status;


    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
