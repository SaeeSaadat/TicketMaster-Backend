package tech.ayot.ticket.backend.integration.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import tech.ayot.ticket.backend.dto.auth.request.LoginRequest;
import tech.ayot.ticket.backend.integration.BaseIntegrationTest;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.user.UserRepository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationServiceIntegrationTest extends BaseIntegrationTest {

    private static final String testName = AuthenticationServiceIntegrationTest.class.getSimpleName();


    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceIntegrationTest(
        FindByIndexNameSessionRepository<? extends Session> sessionRepository,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Test
    public void loginShouldReturn200IfUsernameAndPasswordAreValid() throws Exception {
        String username = testName + "_username";
        String password = testName + "_P@33word";
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        try {
            Instant beforeLogin = Instant.now();
            LoginRequest loginRequest = new LoginRequest(
                username,
                password
            );
            sendRequest(
                HttpMethod.POST,
                "/api/auth/login",
                MediaType.APPLICATION_JSON,
                loginRequest,
                status().isOk()
            );

            Map<String, ? extends Session> sessiosMap = sessionRepository.findByIndexNameAndIndexValue(
                JdbcIndexedSessionRepository.PRINCIPAL_NAME_INDEX_NAME,
                user.getUsername()
            );
            Optional<? extends Session> optionalSession = sessiosMap.values().stream()
                .filter(session -> session.getCreationTime().isAfter(beforeLogin))
                .findFirst();
            Assertions.assertTrue(optionalSession.isPresent());
            sessionRepository.deleteById(optionalSession.get().getId());
        } finally {
            userRepository.delete(user);
        }
    }

    @Test
    public void loginShouldReturn400IfPasswordDoesNotMatchPattern() throws Exception {
        LoginRequest loginRequest = new LoginRequest(
            "username",
            "password"
        );
        sendRequest(
            HttpMethod.POST,
            "/api/auth/login",
            MediaType.APPLICATION_JSON,
            loginRequest,
            status().isBadRequest()
        );
    }

    @Test
    public void loginShouldReturn400IfUsernameDoesNotMatchPattern() throws Exception {
        LoginRequest loginRequest = new LoginRequest(
            "username!",
            "P@33word"
        );
        sendRequest(
            HttpMethod.POST,
            "/api/auth/login",
            MediaType.APPLICATION_JSON,
            loginRequest,
            status().isBadRequest()
        );
    }

    @Test
    public void loginShouldReturn401IfUsernameOrPasswordIsInvalid() throws Exception {
        String username = testName + "_username";
        String password = testName + "_P@33word";
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        try {
            Instant beforeLogin = Instant.now();
            LoginRequest loginRequest = new LoginRequest(
                username,
                password + "X"
            );
            sendRequest(
                HttpMethod.POST,
                "/api/auth/login",
                MediaType.APPLICATION_JSON,
                loginRequest,
                status().isUnauthorized()
            );
        } finally {
            userRepository.delete(user);
        }
    }
}
