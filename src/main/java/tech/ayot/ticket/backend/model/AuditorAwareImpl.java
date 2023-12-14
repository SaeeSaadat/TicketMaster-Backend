package tech.ayot.ticket.backend.model;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<User> {

    private final AuthenticationService authenticationService;

    public AuditorAwareImpl(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @NonNull
    @Override
    public Optional<User> getCurrentAuditor() {
        User user = authenticationService.getCurrentUser();
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}
