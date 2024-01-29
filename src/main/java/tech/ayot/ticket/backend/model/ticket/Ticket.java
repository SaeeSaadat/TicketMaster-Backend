package tech.ayot.ticket.backend.model.ticket;

import jakarta.persistence.*;
import tech.ayot.ticket.backend.model.BaseModel;
import tech.ayot.ticket.backend.model.product.Product;
import tech.ayot.ticket.backend.model.user.User;

import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tickets")
public abstract class Ticket extends BaseModel {

    @Column(length = 64, nullable = false)
    private String title;

    @Column(length = 4096)
    private String description;

    @Column
    private Date deadline;

    @ManyToOne
    private Product product;

    @ManyToOne
    private User user;

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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
