package tech.ayot.ticket.backend.unit.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.ayot.ticket.backend.BackendApplication;
import tech.ayot.ticket.backend.dto.auth.UserDto;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.user.UserRepository;
import tech.ayot.ticket.backend.service.auth.SessionService;
import tech.ayot.ticket.backend.unit.BaseUnitTest;

import static org.mockito.Mockito.*;
import static tech.ayot.ticket.backend.service.auth.SessionService.SPRING_SECURITY_CONTEXT_TOKEN;
import static tech.ayot.ticket.backend.service.auth.SessionService.USERNAME_TOKEN;

@SpringBootTest(classes = {BackendApplication.class})
public class SessionServiceUnitTest extends BaseUnitTest {

    @MockBean
    private final UserRepository userRepository;

    @Mock
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @InjectMocks
    private final SessionService<? extends Session> sessionService;

    public SessionServiceUnitTest(
        UserRepository userRepository,
        FindByIndexNameSessionRepository<? extends Session> sessionRepository,
        SessionService<? extends Session> sessionService
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.sessionService = sessionService;
    }


    @Test
    public void createSessionShouldCreateSession() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession(true)).thenReturn(session);
        Authentication authentication = mock(Authentication.class);
        String username = "username";

        // Act
        sessionService.createSession(request, authentication, username);

        // Assert
        SecurityContext context = SecurityContextHolder.getContext();
        verify(session, times(1)).setAttribute(SPRING_SECURITY_CONTEXT_TOKEN, context);
        verify(session, times(1)).setAttribute(USERNAME_TOKEN, username);
    }

    @Test
    public void updateCurrentSessionShouldUpdateCurrentSession() {
        // Mock session
        HttpSession session = mock(HttpSession.class);
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(attributes.getRequest()).thenReturn(request);
        when(request.getSession(false)).thenReturn(session);
        RequestContextHolder.setRequestAttributes(attributes);

        // Mock context
        SecurityContext context = mock(SecurityContext.class);
        when(session.getAttribute(SPRING_SECURITY_CONTEXT_TOKEN)).thenReturn(context);

        // Create user DTO
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        UserDto userDto = new UserDto(user);

        // Act
        sessionService.updateCurrentSession(userDto);

        // Assert
        verify(context, times(1)).setAuthentication(any(Authentication.class));
        verify(session, times(1)).setAttribute(SPRING_SECURITY_CONTEXT_TOKEN, context);
    }

    @Test
    public void loadUserByUsernameShouldReturnUserDto() {
        // Create user
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        // Mock user repository
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(user);

        // Act
        UserDto userDto = sessionService.loadUserByUsername(user.getUsername());

        // Assert
        Assertions.assertEquals(user.getId(), userDto.getId());
        Assertions.assertEquals(user.getUsername(), userDto.getUsername());
        Assertions.assertEquals(user.getPassword(), userDto.getPassword());
    }
}
