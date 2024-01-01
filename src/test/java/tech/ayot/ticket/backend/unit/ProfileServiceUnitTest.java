package tech.ayot.ticket.backend.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.BackendApplication;
import tech.ayot.ticket.backend.dto.user.request.UpdateProfileRequest;
import tech.ayot.ticket.backend.dto.user.response.ViewProfileResponse;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.user.UserRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;
import tech.ayot.ticket.backend.service.user.ProfileService;

import java.util.UUID;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {BackendApplication.class})
public class ProfileServiceUnitTest extends BaseUnitTest {

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final AuthenticationService authenticationService;

    private final PasswordEncoder passwordEncoder;

    private final ProfileService profileService;

    public ProfileServiceUnitTest(
        UserRepository userRepository,
        AuthenticationService authenticationService,
        PasswordEncoder passwordEncoder, ProfileService profileService
    ) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
    }


    @Test
    public void viewShouldReturnUserProfile() {
        // Create user
        User user = new User();
        user.setVersion(10L);
        user.setUsername("username");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setProfilePicture(UUID.randomUUID());

        // Mock authentication
        when(authenticationService.getCurrentUser()).thenReturn(user);

        // Act
        ResponseEntity<ViewProfileResponse> response = profileService.view();

        // Assert
        ViewProfileResponse viewProfileResponse = response.getBody();
        Assertions.assertNotNull(viewProfileResponse);
        Assertions.assertEquals(user.getVersion(), viewProfileResponse.version());
        Assertions.assertEquals(user.getUsername(), viewProfileResponse.username());
        Assertions.assertEquals(user.getFirstName(), viewProfileResponse.firstName());
        Assertions.assertEquals(user.getLastName(), viewProfileResponse.lastName());
        Assertions.assertEquals(user.getProfilePicture(), viewProfileResponse.profilePicture());
    }

    @Test
    public void updateShouldUpdateUserProfile() {
        // Create user
        User user = new User();
        user.setVersion(10L);
        user.setPassword(passwordEncoder.encode("password"));
        user.setUsername("username");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setProfilePicture(UUID.randomUUID());

        // Mock authentication
        when(authenticationService.getCurrentUser()).thenReturn(user);

        // Create update request
        UpdateProfileRequest request = new UpdateProfileRequest(
            user.getVersion(),
            "updated-firstName",
            "updated-lastName",
            UUID.randomUUID(),
            "password",
            "updated-password",
            "updated-password"
        );

        // Act
        ResponseEntity<ViewProfileResponse> response = profileService.update(request);

        // Assert
        ViewProfileResponse viewProfileResponse = response.getBody();
        Assertions.assertNotNull(viewProfileResponse);
        Assertions.assertEquals(request.version(), viewProfileResponse.version());
        Assertions.assertEquals(user.getUsername(), viewProfileResponse.username());
        Assertions.assertEquals(request.firstName(), viewProfileResponse.firstName());
        Assertions.assertEquals(request.lastName(), viewProfileResponse.lastName());
        Assertions.assertEquals(request.profilePicture(), viewProfileResponse.profilePicture());

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();
        Assertions.assertTrue(passwordEncoder.matches(request.newPassword(), savedUser.getPassword()));
    }

    @Test
    public void updateShouldReturn401IfVersionDoesNotMatch() {
        // Create user
        User user = new User();
        user.setVersion(10L);
        user.setUsername("username");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setProfilePicture(UUID.randomUUID());

        // Mock authentication
        when(authenticationService.getCurrentUser()).thenReturn(user);

        // Create update request
        UpdateProfileRequest request = new UpdateProfileRequest(
            user.getVersion() - 1,
            null,
            null,
            null,
            null,
            null,
            null
        );

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> profileService.update(request)
        );
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, responseStatusException.getStatusCode());
        Assertions.assertEquals("Version is not correct", responseStatusException.getReason());
    }

    @Test
    public void updateShouldReturn401IfPasswordsDoNotMatch() {
        // Create user
        User user = new User();
        user.setVersion(10L);
        user.setUsername("username");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setProfilePicture(UUID.randomUUID());

        // Mock authentication
        when(authenticationService.getCurrentUser()).thenReturn(user);

        // Create update request
        UpdateProfileRequest request = new UpdateProfileRequest(
            user.getVersion(),
            null,
            null,
            null,
            "password",
            "password",
            "password1"
        );

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> profileService.update(request)
        );
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, responseStatusException.getStatusCode());
        Assertions.assertEquals("Password does not match", responseStatusException.getReason());
    }

    @Test
    public void updateShouldReturn401IfOldPasswordDoesNotMatch() {
        // Create user
        User user = new User();
        user.setVersion(10L);
        user.setPassword(passwordEncoder.encode("password"));
        user.setUsername("username");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setProfilePicture(UUID.randomUUID());

        // Mock authentication
        when(authenticationService.getCurrentUser()).thenReturn(user);

        // Create update request
        UpdateProfileRequest request = new UpdateProfileRequest(
            user.getVersion(),
            null,
            null,
            null,
            "password1",
            "password",
            "password"
        );

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> profileService.update(request)
        );
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, responseStatusException.getStatusCode());
        Assertions.assertEquals("Old password is wrong", responseStatusException.getReason());
    }
}
