package tech.ayot.ticket.backend.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateProfileRequest(
    @NotNull Long version,
    @Size(max = 32) String firstName,
    @Size(max = 32) String lastName,
    UUID profilePicture,
    @Size(max = 32) String oldPassword,
    @Size(max = 32) String newPassword,
    @Size(max = 32) String newPasswordConfirmation
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
