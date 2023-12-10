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
    private Integer productId;

    /**
     * The role's title
     */
    private Role role;

    public GrantedRoleDto(
        Integer productId,
        Role role
    ) {
        this.productId = productId;
        this.role = role;
    }


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
