package tech.ayot.ticket.backend.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import tech.ayot.ticket.backend.model.BaseModel;
import java.util.UUID;

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

    @Column(length = 4096)
    private String description;

    @Column(unique = true)
    private UUID imageId;

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

    public UUID getImageId() {
        return imageId;
    }

    public void setImageId(UUID imageId) {
        this.imageId = imageId;
    }
}
