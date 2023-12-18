package tech.ayot.ticket.backend.unit.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.session.Session;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.BackendApplication;
import tech.ayot.ticket.backend.dto.auth.UserDto;
import tech.ayot.ticket.backend.dto.auth.request.LoginRequest;
import tech.ayot.ticket.backend.dto.auth.response.LoginResponse;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;
import tech.ayot.ticket.backend.service.auth.SessionService;
import tech.ayot.ticket.backend.unit.BaseUnitTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {BackendApplication.class})
public class AuthenticationServiceTest extends BaseUnitTest {

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private SessionService<Session> sessionService;

    private final AuthenticationService authenticationService;

    public AuthenticationServiceTest(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @Test
    public void loginShouldReturn200IfUsernameAndPasswordAreValid() {
        // Create user
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // Mock Authentication Manager
        UserDto userDto = new UserDto(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDto);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // Mock method arguments
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        LoginRequest loginRequest = new LoginRequest(
            user.getUsername(),
            user.getPassword()
        );

        ResponseEntity<LoginResponse> responseEntity = authenticationService.login(
            request,
            session,
            loginRequest
        );

        // Assert
        verify(sessionService, times(1))
            .createSession(request, authentication, user.getUsername());
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        LoginResponse loginResponse = responseEntity.getBody();
        Assertions.assertNotNull(loginResponse);
        Assertions.assertEquals(user.getId(), loginResponse.userId());
        Assertions.assertEquals(user.getUsername(), loginResponse.username());
    }

    @Test
    public void loginShouldReturn401IfAccountIsExpired() {
        // Mock Authentication Manager
        when(authenticationManager.authenticate(any())).thenThrow(AccountExpiredException.class);

        // Create method arguments
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        LoginRequest loginRequest = new LoginRequest(
            null,
            null
        );

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> authenticationService.login(
                request,
                session,
                loginRequest
            )
        );
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseStatusException.getStatusCode());
        Assertions.assertEquals("Account is expired", responseStatusException.getReason());
    }

    @Test
    public void loginShouldReturn401IfAccountIsLocked() {
        // Mock Authentication Manager
        when(authenticationManager.authenticate(any())).thenThrow(LockedException.class);

        // Create method arguments
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        LoginRequest loginRequest = new LoginRequest(
            null,
            null
        );

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> authenticationService.login(
                request,
                session,
                loginRequest
            )
        );
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseStatusException.getStatusCode());
        Assertions.assertEquals("Account is locked", responseStatusException.getReason());
    }

    @Test
    public void loginShouldReturn401IfAccountIsDisabled() {
        // Mock Authentication Manager
        when(authenticationManager.authenticate(any())).thenThrow(DisabledException.class);

        // Create method arguments
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        LoginRequest loginRequest = new LoginRequest(
            null,
            null
        );

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> authenticationService.login(
                request,
                session,
                loginRequest
            )
        );
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseStatusException.getStatusCode());
        Assertions.assertEquals("Account is disabled", responseStatusException.getReason());
    }

    @Test
    public void loginShouldReturn401IfUsernameOrPasswordIsNotValid() {
        // Mock Authentication Manager
        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        // Create method arguments
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        LoginRequest loginRequest = new LoginRequest(
            null,
            null
        );

        // Act & Assert
        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> authenticationService.login(
                request,
                session,
                loginRequest
            )
        );
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, responseStatusException.getStatusCode());
        Assertions.assertEquals("Invalid username or password", responseStatusException.getReason());
    }
}
