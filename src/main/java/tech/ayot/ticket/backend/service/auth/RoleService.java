package tech.ayot.ticket.backend.service.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tech.ayot.ticket.backend.dto.auth.UserDto;

/**
 * Role service
 */
@Service
public class RoleService {

    public static final String GUEST = "GUEST";
    public static final String USER = "USER";
    public static final String ADMIN = "ADMIN";

    /**
     * Array of all roles
     * <p>
     *     Make sure to add all roles to this array
     * </p>
     */
    public static final String[] ROLES = {GUEST, USER, ADMIN};


    /**
     * @param productId The product id, null means all products
     * @param roleTitle The role title
     * @return true if the user has the role in the product
     */
    public boolean userHasRole(Integer productId, String roleTitle) {
        // Get user DTO
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.getPrincipal() instanceof UserDto;
        UserDto userDto = isAuthenticated ? (UserDto) authentication.getPrincipal() : null;
        if (userDto == null) return false;

        // Check role
        if (productId == null) {
            return userDto.getRoles().stream().anyMatch(role ->
                role.getProductId() == null && role.getRoleTitle().equals(roleTitle)
            );
        } else {
            return userDto.getRoles().stream().anyMatch(role ->
                (role.getProductId() == null || role.getProductId().equals(productId))
                    && role.getRoleTitle().equals(roleTitle)
            );
        }
    }
}
