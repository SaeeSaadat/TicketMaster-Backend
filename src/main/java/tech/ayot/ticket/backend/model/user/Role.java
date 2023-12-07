package tech.ayot.ticket.backend.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import tech.ayot.ticket.backend.model.BaseModel;

/**
 * Represents an entity for a user role
 */
@Entity
@Table(name = "roles")
public class Role extends BaseModel implements GrantedAuthority {

    /**
     * The role's title
     */
    @Column(nullable = false)
    private String title;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getAuthority() {
        return title;
    }
}
