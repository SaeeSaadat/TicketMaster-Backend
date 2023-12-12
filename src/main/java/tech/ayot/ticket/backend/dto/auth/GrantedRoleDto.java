package tech.ayot.ticket.backend.dto.auth;

import org.springframework.security.core.GrantedAuthority;

/**
 * Data Transfer Object for user role
 */
public class GrantedRoleDto implements GrantedAuthority {

    /**
     * The product's ID
     * <p>
     *     null means all products
     * </p>
     */
    private final Long productId;

    /**
     * The role's title
     */
    private final Role role;

    public GrantedRoleDto(
        Long productId,
        Role role
    ) {
        this.productId = productId;
        this.role = role;
    }


    public Long getProductId() {
        return productId;
    }

    public Role getRole() {
        return role;
    }


    @Override
    public String getAuthority() {
        if (productId == null) {
            return role.getTitle();
        } else {
            return productId + "|" + role.getTitle();
        }
    }
}
