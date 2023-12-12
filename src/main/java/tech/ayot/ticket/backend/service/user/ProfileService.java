package tech.ayot.ticket.backend.service.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.dto.auth.UserDto;
import tech.ayot.ticket.backend.dto.user.UpdateProfileRequest;
import tech.ayot.ticket.backend.dto.user.ViewProfileResponse;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.user.UserRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;

import java.util.Objects;

@RestController
@RequestMapping("/api/profile")
public class ProfileService {

    private final UserRepository userRepository;

    private final AuthenticationService authenticationService;

    private final PasswordEncoder passwordEncoder;

    public ProfileService(
        UserRepository userRepository,
        AuthenticationService authenticationService,
        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * @return View profile response of current logged-in user
     */
    @Transactional(readOnly = true)
    @GetMapping(value = {"/view"})
    public ResponseEntity<ViewProfileResponse> view() {
        User user = authenticationService.getCurrentUser();
        ViewProfileResponse viewProfileResponse = new ViewProfileResponse(
            user.getVersion(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName(),
            user.getProfilePicture()
        );
        return new ResponseEntity<>(viewProfileResponse, HttpStatus.OK);
    }

    /**
     * Updates current logged-in user
     *
     * @param updateProfileRequest The update request
     * @return New view profile response of current logged-in user
     */
    @Transactional
    @PostMapping(value = {"/update"}, consumes = {"application/json"})
    public ResponseEntity<ViewProfileResponse> update(
        @RequestBody @Valid UpdateProfileRequest updateProfileRequest
    ) {
        // Get user
        UserDto userDto = authenticationService.getCurrentUserDto();
        User user = userRepository.getReferenceById(userDto.getId());

        if (!Objects.equals(updateProfileRequest.version(), user.getVersion())) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Version is not correct");
        }

        // Update user fields
        user.setVersion(updateProfileRequest.version());
        user.setFirstName(updateProfileRequest.firstName());
        user.setLastName(updateProfileRequest.lastName());
        user.setProfilePicture(updateProfileRequest.profilePicture());

        // Update password
        String oldPassword = updateProfileRequest.oldPassword();
        String newPassword = updateProfileRequest.newPassword();
        String newPasswordConfirmation = updateProfileRequest.newPasswordConfirmation();
        if (newPassword != null) {
            if (oldPassword == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Old password is wrong");
            }
            if (!newPassword.equals(newPasswordConfirmation)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Password does not match");
            }
            user.setPassword(passwordEncoder.encode(updateProfileRequest.newPassword()));
        }

        // Save user
        userRepository.save(user);

        ViewProfileResponse viewProfileResponse = new ViewProfileResponse(
            user.getVersion(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName(),
            user.getProfilePicture()
        );
        return new ResponseEntity<>(viewProfileResponse, HttpStatus.OK);
    }
}
