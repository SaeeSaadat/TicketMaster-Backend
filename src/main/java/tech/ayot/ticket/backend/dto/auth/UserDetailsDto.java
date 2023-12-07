package tech.ayot.ticket.backend.dto.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;

public class UserDetailsDto implements UserDetails {

    private final Integer id;

    private final String username;

    private final String password;

    private final Date modifiedDate;

    public UserDetailsDto(
        Integer id,
        String username,
        String password,
        Date modifiedDate
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.modifiedDate = modifiedDate;
    }


    public Integer id() {
        return id;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
}
