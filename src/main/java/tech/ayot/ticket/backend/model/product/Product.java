package tech.ayot.ticket.backend.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import tech.ayot.ticket.backend.model.BaseModel;

/**
 * Represents an entity for a product
 */
@Entity
@Table(name = "products")
public class Product extends BaseModel {

    /**
     * The product's name
     */
    @Column(unique = true, nullable = false)
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
