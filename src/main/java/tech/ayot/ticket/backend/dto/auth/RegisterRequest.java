package tech.ayot.ticket.backend.dto.auth;


import jakarta.validation.constraints.NotNull;

/**
 * Request body of register request.
 *
 * @see tech.ayot.ticket.backend.service.auth.AuthenticationService AuthenticationService
 * @see tech.ayot.ticket.backend.model.user.User User
 */
public record RegisterRequest(
    @NotNull String username,
    @NotNull String password
) {

    public RegisterRequest(
        String username,
        String password
    ) {
        this.username = username;
        this.password = password;
    }
}
