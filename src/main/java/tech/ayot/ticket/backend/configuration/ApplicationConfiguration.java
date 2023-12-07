package tech.ayot.ticket.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import tech.ayot.ticket.backend.model.AuditorAwareImpl;
import tech.ayot.ticket.backend.repository.user.UserRepository;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class ApplicationConfiguration {

    private final UserRepository userRepository;

    public ApplicationConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Bean
    public AuditorAwareImpl auditorProvider() {
        return new AuditorAwareImpl(userRepository);
    }
}
