package tech.ayot.ticket.backend.model.ticket;

import jakarta.persistence.*;
import org.springframework.data.annotation.Reference;
import tech.ayot.ticket.backend.model.BaseModel;
import tech.ayot.ticket.backend.model.product.Product;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tickets")
public abstract class Ticket extends BaseModel {

    @Column(length = 64, nullable = false)
    private String name;

    @Column(length = 4096)
    private String description;

    @ManyToOne
    private Product product;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
