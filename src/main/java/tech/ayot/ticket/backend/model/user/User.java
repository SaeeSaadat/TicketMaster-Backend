package tech.ayot.ticket.backend.model.user;

import jakarta.persistence.*;
import org.hibernate.envers.AuditMappedBy;
import tech.ayot.ticket.backend.model.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an entity for a user
 */
@Entity
@Table(name = "users")
public class User extends BaseModel {

    /**
     * The user's username
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * The user's encoded password
     */
    @Column(nullable = false)
    private String password;

    /**
     * Represents relationship between the user and products
     */
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @AuditMappedBy(mappedBy = "user")
    @JoinColumn(name = "user_id")
    List<UserProduct> userProducts = new ArrayList<>();


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserProduct> getUserProducts() {
        return userProducts;
    }

    public void setUserProducts(List<UserProduct> userProducts) {
        this.userProducts = userProducts;
    }
}
