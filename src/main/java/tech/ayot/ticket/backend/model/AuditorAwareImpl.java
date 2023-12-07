package tech.ayot.ticket.backend.model;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import tech.ayot.ticket.backend.dto.auth.UserDto;
import tech.ayot.ticket.backend.model.user.User;
import tech.ayot.ticket.backend.repository.user.UserRepository;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<User> {

    private final UserRepository userRepository;

    public AuditorAwareImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @NonNull
    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Return empty if user does not exist
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDto userDto)) {
            return Optional.empty();
        }

        User user = userRepository.findUserByUsername(userDto.getUsername());
        return Optional.of(user);
    }
}
