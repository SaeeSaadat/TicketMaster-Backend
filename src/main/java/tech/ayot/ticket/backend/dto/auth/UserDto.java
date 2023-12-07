package tech.ayot.ticket.backend.dto.auth;

import org.springframework.security.core.userdetails.UserDetails;
import tech.ayot.ticket.backend.model.user.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for user entity
 *
 * @see User
 */
public class UserDto implements UserDetails {

    private final Integer id;

    private final String username;

    private final String password;

    private final Date modifiedDate;

    private final List<GrantedRoleDto> roles;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.modifiedDate = user.getLastModifiedDate();
        this.roles = user.getUserProducts().stream().map(userProduct -> {
                Integer productId = null;
                if (userProduct.getProduct() != null) {
                    productId = userProduct.getProduct().getId();
                }
                return new GrantedRoleDto(
                    productId,
                    userProduct.getRole().getTitle()
                );
            }
        ).collect(Collectors.toList());
    }


    public Integer id() {
        return id;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }


    @Override
    public Collection<GrantedRoleDto> getAuthorities() {
        return roles;
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
