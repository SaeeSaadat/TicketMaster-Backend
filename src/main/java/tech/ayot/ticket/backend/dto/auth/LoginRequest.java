package tech.ayot.ticket.backend.dto.auth;

import javax.validation.constraints.NotNull;

/**
 * Request body of login request.
 *
 * @see tech.ayot.ticket.backend.service.auth.AuthenticationService AuthenticationService
 * @see tech.ayot.ticket.backend.model.user.User User
 */
public class LoginRequest {

    @NotNull
    private String username;

    @NotNull
    private String password;

    public LoginRequest(
        String username,
        String password
    ) {
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
