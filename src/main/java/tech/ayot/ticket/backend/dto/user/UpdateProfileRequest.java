package tech.ayot.ticket.backend.dto.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateProfileRequest(
    @NotNull Long version,
    String firstName,
    String lastName,
    UUID profilePicture,
    String oldPassword,
    String newPassword,
    String newPasswordConfirmation
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
