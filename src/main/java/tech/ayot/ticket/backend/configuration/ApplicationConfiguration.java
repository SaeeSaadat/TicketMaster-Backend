package tech.ayot.ticket.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import tech.ayot.ticket.backend.model.AuditorAwareImpl;
import tech.ayot.ticket.backend.service.auth.AuthenticationService;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class ApplicationConfiguration {

    private final AuthenticationService authenticationService;

    public ApplicationConfiguration(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @Bean
    public AuditorAwareImpl auditorProvider() {
        return new AuditorAwareImpl(authenticationService);
    }
}
