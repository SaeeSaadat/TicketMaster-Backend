package tech.ayot.ticket.backend.dto.auth.request;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import tech.ayot.ticket.backend.model.user.User;

/**
 * Request body of register request.
 *
 * @see tech.ayot.ticket.backend.service.auth.AuthenticationService AuthenticationService
 * @see tech.ayot.ticket.backend.model.user.User User
 */
public record RegisterRequest(
    @NotNull @Pattern(regexp = User.USERNAME_REGEX) String username,
    @NotNull @Pattern(regexp = User.PASSWORD_REGEX) String password
) {

    public RegisterRequest(
        String username,
        String password
    ) {
        this.username = username;
        this.password = password;
    }
}
