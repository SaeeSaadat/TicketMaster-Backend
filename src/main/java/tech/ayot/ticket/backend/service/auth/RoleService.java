package tech.ayot.ticket.backend.service.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tech.ayot.ticket.backend.dto.auth.enumuration.Role;
import tech.ayot.ticket.backend.dto.auth.UserDto;

/**
 * Role service
 */
@Service
public class RoleService {

    /**
     * @param productId The product id, null means all products
     * @param role The role
     * @return true if the user has the role in the product
     */
    public boolean userHasRole(Long productId, Role role) {
        // Get user DTO
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.getPrincipal() instanceof UserDto;
        UserDto userDto = isAuthenticated ? (UserDto) authentication.getPrincipal() : null;
        if (userDto == null) return false;

        // Check role
        if (productId == null) {
            return userDto.getRoles().stream().anyMatch(grantedRole ->
                grantedRole.getProductId() == null && grantedRole.getRole().getLevel() >= role.getLevel()
            );
        } else {
            return userDto.getRoles().stream().anyMatch(grantedRole ->
                (grantedRole.getProductId() == null || grantedRole.getProductId().equals(productId))
                    && grantedRole.getRole().getLevel() >= role.getLevel()
            );
        }
    }
}
