package tech.ayot.ticket.backend.dto.auth;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body of register request.
 *
 * @see tech.ayot.ticket.backend.service.auth.AuthenticationService AuthenticationService
 * @see tech.ayot.ticket.backend.model.user.User User
 */
public record RegisterRequest(
    @NotBlank @Size(max = 32) String username,
    @NotBlank @Size(max = 32) String password
) {

    public RegisterRequest(
        String username,
        String password
    ) {
        this.username = username;
        this.password = password;
    }
}
