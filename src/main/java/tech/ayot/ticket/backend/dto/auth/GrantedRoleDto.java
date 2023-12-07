package tech.ayot.ticket.backend.dto.auth;

import org.springframework.security.core.GrantedAuthority;

public class GrantedRoleDto implements GrantedAuthority {

    private Integer productId;

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
