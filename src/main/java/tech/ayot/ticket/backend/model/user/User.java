package tech.ayot.ticket.backend.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.springframework.security.core.userdetails.UserDetails;
import tech.ayot.ticket.backend.dto.auth.UserDetailsDto;
import tech.ayot.ticket.backend.model.BaseModel;

@Entity
@Table(name = "users")
public class User extends BaseModel {

    @Column(unique = true, nullable = false)
    protected String username;

    @Column(nullable = false)
    protected String password;


    public UserDetails toUserDetails() {
        return new UserDetailsDto(
            this.id,
            this.username,
            this.password,
            this.lastModifiedDate
        );
    }


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
}
