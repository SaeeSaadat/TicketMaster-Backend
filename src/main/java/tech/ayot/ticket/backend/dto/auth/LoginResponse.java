package tech.ayot.ticket.backend.dto.auth;

/**
 * Response body of login and user request.
 *
 * @see tech.ayot.ticket.backend.service.auth.AuthenticationService AuthenticationService
 * @see tech.ayot.ticket.backend.model.user.User User
 */
public record LoginResponse(
    Long userId,
    String username
) {
}
