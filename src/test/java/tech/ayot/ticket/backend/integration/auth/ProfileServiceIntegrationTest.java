package tech.ayot.ticket.backend.integration.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import tech.ayot.ticket.backend.MockMvcResponse;
import tech.ayot.ticket.backend.dto.user.request.UpdateProfileRequest;
import tech.ayot.ticket.backend.dto.user.response.ViewProfileResponse;
import tech.ayot.ticket.backend.integration.BaseIntegrationTest;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.user.UserRepository;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileServiceIntegrationTest extends BaseIntegrationTest {

    private final UserRepository userRepository;

    public ProfileServiceIntegrationTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @AfterEach
    public void cleanUp() {
        // Get all users but admin user
        List<User> users = userRepository.findAll();
        users = users.stream().filter(user -> !ADMIN_USER.equals(user.getUsername())).toList();

        // Delete users
        userRepository.deleteAll(users);
    }

    @Test
    public void viewShouldReturn200AndProfileUser() throws Exception {
        MockMvcResponse<ViewProfileResponse> mockMvcResponse = sendRequest(
            HttpMethod.GET,
            "/api/profile/view",
            MediaType.APPLICATION_JSON,
            null,
            status().isOk(),
            ViewProfileResponse.class
        );
        ViewProfileResponse response = mockMvcResponse.body();
        Assertions.assertEquals(ADMIN_USER, response.username());
    }

    @Test
    public void updateShouldReturn200AndUpdateUser() throws Exception {
        User user = userRepository.findUserByUsername(ADMIN_USER);
        String firstName = "firstName";
        String lastName = "lastName";
        UpdateProfileRequest request = new UpdateProfileRequest(
            user.getVersion(),
            firstName,
            lastName,
            null,
            null,
            null,
            null
        );

        MockMvcResponse<ViewProfileResponse> mockMvcResponse = sendRequest(
            HttpMethod.POST,
            "/api/profile/update",
            MediaType.APPLICATION_JSON,
            request,
            status().isOk(),
            ViewProfileResponse.class
        );

        ViewProfileResponse response = mockMvcResponse.body();
        Assertions.assertEquals(ADMIN_USER, response.username());
        Assertions.assertEquals(firstName, response.firstName());
        Assertions.assertEquals(lastName, response.lastName());
    }

    @Test
    public void updateShouldReturn406IfVersionIsNotValid() throws Exception {
        User user = userRepository.findUserByUsername(ADMIN_USER);
        String firstName = "firstName";
        String lastName = "lastName";
        UpdateProfileRequest request = new UpdateProfileRequest(
            user.getVersion() - 1,
            firstName,
            lastName,
            null,
            null,
            null,
            null
        );

        sendRequest(
            HttpMethod.POST,
            "/api/profile/update",
            MediaType.APPLICATION_JSON,
            request,
            status().isNotAcceptable()
        );
    }

    @Test
    public void updateShouldReturn426IfPatternsDoNotMatch() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest(
            0L,
            "first-name",
            null,
            null,
            null,
            null,
            null
        );

        sendRequest(
            HttpMethod.POST,
            "/api/profile/update",
            MediaType.APPLICATION_JSON,
            request,
            status().isBadRequest()
        );
    }

    @Test
    public void updateShouldReturn406IfPasswordsDoesNotMatch() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest(
            0L,
            null,
            null,
            null,
            "P@33word",
            "P@33word",
            "P@33word2"
        );

        sendRequest(
            HttpMethod.POST,
            "/api/profile/update",
            MediaType.APPLICATION_JSON,
            request,
            status().isNotAcceptable()
        );
    }

    @Test
    public void updateShouldReturn406IfOldPasswordIsNotCorrect() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest(
            0L,
            null,
            null,
            null,
            "P@33word",
            "P@33word",
            "P@33word"
        );

        sendRequest(
            HttpMethod.POST,
            "/api/profile/update",
            MediaType.APPLICATION_JSON,
            request,
            status().isNotAcceptable()
        );
    }
}
