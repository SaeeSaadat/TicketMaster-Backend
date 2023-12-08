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
    private String roleTitle;

    public GrantedRoleDto(
        Integer productId,
        String roleTitle
    ) {
        this.productId = productId;
        this.roleTitle = roleTitle;
    }


    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }


    @Override
    public String getAuthority() {
        if (productId == null) {
            return roleTitle;
        } else {
            return productId + "|" + roleTitle;
        }
    }
}
