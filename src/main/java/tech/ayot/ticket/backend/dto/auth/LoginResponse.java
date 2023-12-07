package tech.ayot.ticket.backend.dto.auth;

/**
 * Response body of login and user request.
 *
 * @see tech.ayot.ticket.backend.service.auth.AuthenticationService AuthenticationService
 * @see tech.ayot.ticket.backend.model.user.User User
 */
public class LoginResponse {

    private Integer userId;

    private String username;

    public LoginResponse(Integer userId, String username) {
        this.userId = userId;
        this.username = username;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
