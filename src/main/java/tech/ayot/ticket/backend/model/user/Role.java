package tech.ayot.ticket.backend.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import tech.ayot.ticket.backend.model.BaseModel;

import java.util.Objects;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(title, role.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
