package tech.ayot.ticket.backend.dto.user.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import tech.ayot.ticket.backend.model.user.User;

import java.util.UUID;

public record UpdateProfileRequest(
    @NotNull Long version,
    @Pattern(regexp = "[a-zA-Z]{1,32}") String firstName,
    @Pattern(regexp = "[a-zA-Z]{1,32}") String lastName,
    UUID profilePicture,
    @Pattern(regexp = User.PASSWORD_REGEX) String oldPassword,
    @Pattern(regexp = User.PASSWORD_REGEX) String newPassword,
    @Pattern(regexp = User.PASSWORD_REGEX) String newPasswordConfirmation
) {

    public UpdateProfileRequest(
        Long version,
        String firstName,
        String lastName,
        UUID profilePicture,
        String oldPassword,
        String newPassword,
        String newPasswordConfirmation
    ) {
        this.version = version;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePicture = profilePicture;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
}
