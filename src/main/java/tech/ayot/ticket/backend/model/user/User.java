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

    public static final String USERNAME_REGEX = "[a-zA-Z0-9_]{1,64}";

    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[-_@$!%*?&])[-_A-Za-z\\d@$!%*?&]{8,64}$";


    /**
     * The user's username
     */
    @Column(unique = true, nullable = false, length = 64)
    private String username;

    /**
     * The user's encoded password
     */
    @Column(nullable = false, length = 64)
    private String password;

    /**
     * Represents relationship between the user and products
     */
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @AuditMappedBy(mappedBy = "user")
    @JoinColumn(name = "user_id")
    List<UserProduct> userProducts = new ArrayList<>();

    /**
     * The user's first name
     */
    @Column(length = 64)
    private String firstName;

    /**
     * The user's last name
     */
    @Column(length = 64)
    private String lastName;

    /**
     * The user's profile picture
     */
    @Column
    private String profilePicture;



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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
