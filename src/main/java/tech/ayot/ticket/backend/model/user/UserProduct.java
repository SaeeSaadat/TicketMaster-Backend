package tech.ayot.ticket.backend.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import tech.ayot.ticket.backend.model.BaseModel;
import tech.ayot.ticket.backend.model.product.Product;

/**
 * Represents relationship between a user and a product
 */
@Entity
@Table(name = "user_products", indexes = @Index(columnList = "user_id,product_id", unique = true))
public class UserProduct extends BaseModel {

    /**
     * The user
     */
    @ManyToOne
    private User user;

    /**
     * The product
     * <p>
     *     Null means all products
     * </p>
     */
    @ManyToOne
    private Product product;


    /**
     * The user's role in the product
     */
    @ManyToOne
    private Role role;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
