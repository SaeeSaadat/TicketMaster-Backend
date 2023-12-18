package tech.ayot.ticket.backend.unit.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.Session;
import org.springframework.web.server.ResponseStatusException;
import tech.ayot.ticket.backend.BackendApplication;
import tech.ayot.ticket.backend.dto.auth.UserDto;
import tech.ayot.ticket.backend.dto.auth.request.LoginRequest;
import tech.ayot.ticket.backend.dto.auth.request.RegisterRequest;
import tech.ayot.ticket.backend.dto.auth.response.LoginResponse;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.user.UserRepository;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;
import tech.ayot.ticket.backend.service.auth.SessionService;
import tech.ayot.ticket.backend.unit.BaseUnitTest;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {BackendApplication.class})
public class AuthenticationServiceUnitTest extends BaseUnitTest {

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private SessionService<Session> sessionService;

    @MockBean
    private UserRepository userRepository;

    private final AuthenticationService authenticationService;

    public AuthenticationServiceUnitTest(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @Test
    public void loginShouldReturn200AndCreateSession() {
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

        // Create method arguments
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

    @Test
    public void logoutShouldReturn200() {
        // Create method arguments
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        ResponseEntity<Void> responseEntity = authenticationService.logout(request, response);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void registerShouldReturn200AndCreateUser() {
        // Create user
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // Mock user repository
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(null);

        // Create method arguments
        RegisterRequest registerRequest = new RegisterRequest(
            user.getUsername(),
            user.getPassword()
        );

        ResponseEntity<Void> responseEntity = authenticationService.register(registerRequest);

        verify(userRepository, times(1)).save(any(User.class));
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void registerShouldReturn409IfUsernameExists() {
        // Create user
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // Mock user repository
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(user);

        // Create method arguments
        RegisterRequest registerRequest = new RegisterRequest(
            user.getUsername(),
            user.getPassword()
        );

        ResponseStatusException responseStatusException = Assertions.assertThrows(
            ResponseStatusException.class,
            () -> authenticationService.register(registerRequest)
        );
        Assertions.assertEquals(HttpStatus.CONFLICT, responseStatusException.getStatusCode());
        Assertions.assertEquals("Username already exists", responseStatusException.getReason());
    }

    @Test
    public void currentUserShouldReturnCurrentUser() {
        // Create user
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // Mock authentication
        UserDto userDto = new UserDto(user);
        user.setLastModifiedDate(new Date());
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDto);

        // Mock context holder
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        // Mock user repository
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        ResponseEntity<LoginResponse> responseEntity = authenticationService.currentUser();

        // Assert
        verify(sessionService, times(1)).updateCurrentSession(any(UserDto.class));
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        LoginResponse loginResponse = responseEntity.getBody();
        Assertions.assertNotNull(loginResponse);
        Assertions.assertEquals(user.getId(), loginResponse.userId());
        Assertions.assertEquals(user.getUsername(), loginResponse.username());
    }

    @Test
    public void currentUserShouldReturnNullIfUserIsNotLoggedIn() {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(null);

        // Mock context holder
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        ResponseEntity<LoginResponse> responseEntity = authenticationService.currentUser();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        LoginResponse loginResponse = responseEntity.getBody();
        Assertions.assertNotNull(loginResponse);
        Assertions.assertNull(loginResponse.userId());
        Assertions.assertNull(loginResponse.username());
    }
}
