package tech.ayot.ticket.backend.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.user.UserRepository;

import java.util.Map;

/**
 * Session Service
 * @param <S> Session
 */
@Service
@Transactional(readOnly = true)
public class SessionService<S extends Session> implements UserDetailsService {

    private static final String SPRING_SECURITY_CONTEXT_TOKEN = "SPRING_SECURITY_CONTEXT";

    private static final String USERNAME_TOKEN = JdbcIndexedSessionRepository.PRINCIPAL_NAME_INDEX_NAME;

    private final FindByIndexNameSessionRepository<S> sessionRepository;

    private final UserRepository userRepository;

    public SessionService(
        FindByIndexNameSessionRepository<S> sessionRepository,
        UserRepository userRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }


    /**
     * @param request        The HTTP request
     * @param authentication The authentication object
     * @param username       The user's username
     */
    public void createSession(
        HttpServletRequest request,
        Authentication authentication,
        String username
    ) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_TOKEN, securityContext);
        session.setAttribute(USERNAME_TOKEN, username);
    }

    /**
     * Updates all session for the given user
     * @param user The user to update its sessions
     */
    public void updateAllSessions(UserDetails user) {
        Map<String, S> sessionsMap = sessionRepository.findByIndexNameAndIndexValue(
            USERNAME_TOKEN,
            user.getUsername()
        );
        sessionsMap.values().forEach(session -> {
            SecurityContext securityContext = session.getAttribute(SPRING_SECURITY_CONTEXT_TOKEN);
            PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
                user,
                user.getPassword(),
                user.getAuthorities()
            );
            securityContext.setAuthentication(authentication);
            session.setAttribute(SPRING_SECURITY_CONTEXT_TOKEN, securityContext);
            sessionRepository.save(session);
        });
    }

    /**
     * Updates current session for the given user
     * @param user The user to update its current session
     */
    public void updateCurrentSession(UserDetails user) {
        HttpSession session = getCurrentSession();
        SecurityContext securityContext = (SecurityContext) session.getAttribute(SPRING_SECURITY_CONTEXT_TOKEN);
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
            user,
            user.getPassword(),
            user.getAuthorities()
        );
        securityContext.setAuthentication(authentication);
        session.setAttribute(SPRING_SECURITY_CONTEXT_TOKEN, securityContext);
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username.toLowerCase());
        return user.toUserDetails();
    }


    private HttpSession getCurrentSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(false);
    }
}
