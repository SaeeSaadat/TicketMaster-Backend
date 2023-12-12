package tech.ayot.ticket.backend.dto.auth;

import javax.validation.constraints.NotNull;

/**
 * Request body of login request.
 *
 * @see tech.ayot.ticket.backend.service.auth.AuthenticationService AuthenticationService
 * @see tech.ayot.ticket.backend.model.user.User User
 */
public record LoginRequest(
    @NotNull String username,
    @NotNull String password
) {

    public LoginRequest(
        String username,
        String password
    ) {
        this.username = username;
        this.password = password;
    }
}
