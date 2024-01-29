package tech.ayot.ticket.backend.dto.auth.response;

import tech.ayot.ticket.backend.model.enumuration.Role;

/**
 * Response body of login and user request.
 *
 * @see tech.ayot.ticket.backend.service.auth.AuthenticationService AuthenticationService
 * @see tech.ayot.ticket.backend.model.user.User User
 */
public record LoginResponse(
    Long userId,
    String username,
    Long productId,
    Role role,
    Role rootRole
) {
}
